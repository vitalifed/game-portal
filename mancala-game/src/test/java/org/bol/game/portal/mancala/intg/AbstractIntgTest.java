package org.bol.game.portal.mancala.intg;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.bol.game.portal.mancala.dto.MancalaUser;
import org.bol.game.portal.mancala.intg.builder.Operation;
import org.bol.game.portal.mancala.intg.builder.Validation;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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
public class AbstractIntgTest {

	@LocalServerPort
	private int port;


	public int getPort() {
		return port;
	}
	
	private static SockJsClient sockJsClient;
	private static WebSocketStompClient stompClient;
	static {
		sockJsClient = new SockJsClient(Arrays.asList(new WebSocketTransport(new StandardWebSocketClient())));

		stompClient = new WebSocketStompClient(sockJsClient);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
	}
	
	private final AtomicReference<Throwable> failure = new AtomicReference<>();

	public AtomicReference<Throwable> getFailure() {
		return failure;
	}

	public static WebSocketStompClient getStompClient() {
		return stompClient;
	}
	
	protected Validation<AtomicReference<Throwable>, CountDownLatch, Boolean> check() {
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
	
	protected Operation<StompSession, CountDownLatch, String, String, Boolean> operation() {
		return operation("create");
	}

	protected Operation<StompSession, CountDownLatch, String, String, Boolean> operation(String cmd) {
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

	protected Operation<StompSession, CountDownLatch, String, String, Boolean> step(final int idx) {
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
