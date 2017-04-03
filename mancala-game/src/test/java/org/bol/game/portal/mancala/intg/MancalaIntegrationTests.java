package org.bol.game.portal.mancala.intg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.bol.game.portal.dto.Room;
import org.bol.game.portal.mancala.dto.MancalaUser;
import org.bol.game.portal.mancala.flow.RemoteCommand;
import org.bol.game.portal.mancala.intg.builder.Operation;
import org.bol.game.portal.mancala.intg.builder.Validation;
import org.bol.game.portal.mancala.intg.builder.WorkflowLauncher;
import org.bol.game.portal.mancala.intg.builder.WorkflowTestBuilder;
import org.bol.game.portal.mancala.intg.handler.CommandStompHandler;
import org.bol.game.portal.mancala.repository.MancalaRoomRepository;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MancalaIntegrationTests {

	@LocalServerPort
	private int port;

	@Autowired
	@Qualifier("roomRepository")
	private MancalaRoomRepository roomRepository;

	private static SockJsClient sockJsClient;
	private static WebSocketStompClient stompClient;
	static {
		sockJsClient = new SockJsClient(Arrays.asList(new WebSocketTransport(new StandardWebSocketClient())));

		stompClient = new WebSocketStompClient(sockJsClient);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
	}
	private String roomName1 = "roomName1";
	private String roomName2 = "roomName2";
	private String token1 = "token1";
	private String token2 = "token2";
	private String token3 = "token3";

	final AtomicReference<Throwable> failure = new AtomicReference<>();

	@Test
	public void subscribeAndPlay() throws Exception {

		///////////////////////////////////
		// Create a room and subscribe
		///////////////////////////////////
		WorkflowLauncher launcher = new WorkflowTestBuilder().token(token1).room(roomName1).client(stompClient)
				.commands(RemoteCommand.SUBSCRIBE).failure(failure).port(port).operation(operation()).validation(check())
				.build(1).launch().validate();

		// check(launcher.getLatch());
		assertEquals(1, roomRepository.get(new Room(roomName1)).getState().size());

		///////////////////////////////////
		// Create a room, subscribe, start and move
		///////////////////////////////////

		WorkflowLauncher launcher2 = new WorkflowTestBuilder().token(token2).room(roomName1).client(stompClient)
				.commands(RemoteCommand.SUBSCRIBE, RemoteCommand.START).failure(failure).port(port).operation(operation())
				.validation(check()).build(4);

		launcher.getHandler()
				.setActualHandler(new CommandStompHandler(failure, launcher2.getLatch(), RemoteCommand.START));

		launcher2.launch().validate();
		assertEquals(2, roomRepository.get(new Room(roomName1)).getState().size());

		///////////////////////////////////
		// Make the first move
		///////////////////////////////////
		CountDownLatch latch = new CountDownLatch(3);

		launcher.getHandler().setActualHandler(new CommandStompHandler(failure, latch, RemoteCommand.STATE));
		launcher2.getHandler().setActualHandler(new CommandStompHandler(failure, latch, RemoteCommand.STATE));

		launcher.setOperation(step(0));
		launcher.setLatch(latch);
		launcher.launch().validate();

		///////////////////////////////////
		// Try to subscribe on existing room, the test expects refusal
		///////////////////////////////////
		new WorkflowTestBuilder().token(token3).room(roomName1).client(stompClient).commands(RemoteCommand.REFUSE)
				.failure(failure).port(port).operation(operation()).validation(check()).build(1).launch().validate();

		assertEquals(2, roomRepository.get(new Room(roomName1)).getState().size());

		///////////////////////////////////
		// Try to create a room with another name
		///////////////////////////////////
		new WorkflowTestBuilder().token(token3).room(roomName2).client(stompClient).commands(RemoteCommand.SUBSCRIBE)
				.failure(failure).port(port).operation(operation()).validation(check()).build(1).launch().validate();

		assertEquals(1, roomRepository.get(new Room(roomName2)).getState().size());

		///////////////////////////////////
		// Finish a game
		///////////////////////////////////

		roomRepository.get(new Room(roomName1)).getState().get(new MancalaUser("Vitali", token1))
				.setState(new int[] { 0, 0, 0, 0, 0, 1 });
		latch = new CountDownLatch(4);

		launcher.getHandler()
				.setActualHandler(new CommandStompHandler(failure, latch, RemoteCommand.STOP, RemoteCommand.STATE));
		launcher2.getHandler()
				.setActualHandler(new CommandStompHandler(failure, latch, RemoteCommand.STATE, RemoteCommand.STOP));

		launcher.setOperation(step(5));
		launcher.setLatch(latch);
		launcher.launch().validate();

		///////////////////////////////////
		// Finish a game
		///////////////////////////////////

		latch = new CountDownLatch(1);

		launcher2.getHandler()
				.setActualHandler(new CommandStompHandler(failure, latch, RemoteCommand.DISCONNECT));

		launcher.setOperation(operation("leave"));
		launcher.setLatch(latch);
		launcher.launch().validate();
		
		assertNull(roomRepository.get(new Room(roomName1)));
	}

	private Validation<AtomicReference<Throwable>, CountDownLatch, Boolean> check() {
		return (failure, latch) -> {
			if (latch.await(10, TimeUnit.SECONDS)) {
				if (failure.get() != null) {
					throw new AssertionError("", failure.get());
				}
			} else {
				fail("Command not received");
			}
			return true;
		};
	}
	
	private Operation<StompSession, CountDownLatch, String, String, Boolean> operation() {
		return operation("create");
	}

	private Operation<StompSession, CountDownLatch, String, String, Boolean> operation(String cmd) {
		return (session, latch, token, roomName) -> {
			try {
				MancalaUser user = new MancalaUser("Vitali");
				user.setToken(token);

				session.send("/mancala/room/" + roomName + "/"+cmd, user);
			} catch (Throwable t) {
				failure.set(t);
				latch.countDown();
				session.disconnect();
			}
			return true;
		};
	}

	private Operation<StompSession, CountDownLatch, String, String, Boolean> step(final int idx) {
		return (session, latch, token, roomName) -> {
			try {
				MancalaUser user = new MancalaUser("Vitali");
				user.setToken(token);

				session.send("/mancala/room/" + roomName + "/step/" + idx, user);
			} catch (Throwable t) {
				failure.set(t);
				latch.countDown();
				session.disconnect();
			}
			return true;
		};
	}

}
