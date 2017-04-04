package org.bol.game.portal.mancala.intg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.concurrent.CountDownLatch;

import org.bol.game.portal.dto.Room;
import org.bol.game.portal.mancala.dto.MancalaUser;
import org.bol.game.portal.mancala.flow.RemoteCommand;
import org.bol.game.portal.mancala.intg.builder.WorkflowLauncher;
import org.bol.game.portal.mancala.intg.builder.WorkflowTestBuilder;
import org.bol.game.portal.mancala.intg.handler.CommandStompHandler;
import org.bol.game.portal.mancala.repository.MancalaRoomRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class MancalaIntegrationTests extends AbstractIntgTest{

	@Autowired
	@Qualifier("roomRepository")
	private MancalaRoomRepository roomRepository;

	private String roomName1 = "roomName1";
	private String roomName2 = "roomName2";
	private String token1 = "token1";
	private String token2 = "token2";
	private String token3 = "token3";

	@Test
	public void subscribeAndPlay() throws Exception {

		///////////////////////////////////
		// Create a room and subscribe
		///////////////////////////////////
		WorkflowLauncher launcher = new WorkflowTestBuilder().token(token1).room(roomName1).client(getStompClient())
				.commands(RemoteCommand.SUBSCRIBE).failure(getFailure()).port(getPort()).operation(operation()).validation(check())
				.build(1).launch().validate();

		assertEquals(1, roomRepository.get(new Room(roomName1)).getState().size());

		///////////////////////////////////
		// Create a room, subscribe, start and move
		///////////////////////////////////

		WorkflowLauncher launcher2 = new WorkflowTestBuilder().token(token2).room(roomName1).client(getStompClient())
				.commands(RemoteCommand.SUBSCRIBE, RemoteCommand.START).failure(getFailure()).port(getPort()).operation(operation())
				.validation(check()).build(4);

		launcher.getHandler()
				.setActualHandler(new CommandStompHandler(getFailure(), launcher2.getLatch(), RemoteCommand.START));

		launcher2.launch().validate();
		assertEquals(2, roomRepository.get(new Room(roomName1)).getState().size());

		///////////////////////////////////
		// Make the first move
		///////////////////////////////////
		CountDownLatch latch = new CountDownLatch(3);

		launcher.getHandler().setActualHandler(new CommandStompHandler(getFailure(), latch, RemoteCommand.STATE));
		launcher2.getHandler().setActualHandler(new CommandStompHandler(getFailure(), latch, RemoteCommand.STATE));

		launcher.setOperation(step(0));
		launcher.setLatch(latch);
		launcher.launch().validate();

		///////////////////////////////////
		// Try to subscribe on existing room, the test expects refusal
		///////////////////////////////////
		new WorkflowTestBuilder().token(token3).room(roomName1).client(getStompClient()).commands(RemoteCommand.REFUSE)
				.failure(getFailure()).port(getPort()).operation(operation()).validation(check()).build(1).launch().validate();

		assertEquals(2, roomRepository.get(new Room(roomName1)).getState().size());

		///////////////////////////////////
		// Try to create a room with another name
		///////////////////////////////////
		new WorkflowTestBuilder().token(token3).room(roomName2).client(getStompClient()).commands(RemoteCommand.SUBSCRIBE)
				.failure(getFailure()).port(getPort()).operation(operation()).validation(check()).build(1).launch().validate();

		assertEquals(1, roomRepository.get(new Room(roomName2)).getState().size());

		///////////////////////////////////
		// Finish a game
		///////////////////////////////////

		roomRepository.get(new Room(roomName1)).getState().get(new MancalaUser("Vitali", token1))
				.setState(new int[] { 0, 0, 0, 0, 0, 1 });
		latch = new CountDownLatch(4);

		launcher.getHandler()
				.setActualHandler(new CommandStompHandler(getFailure(), latch, RemoteCommand.STOP, RemoteCommand.STATE));
		launcher2.getHandler()
				.setActualHandler(new CommandStompHandler(getFailure(), latch, RemoteCommand.STATE, RemoteCommand.STOP));

		launcher.setOperation(step(5));
		launcher.setLatch(latch);
		launcher.launch().validate();

		///////////////////////////////////
		// Finish a game
		///////////////////////////////////

		latch = new CountDownLatch(1);

		launcher2.getHandler()
				.setActualHandler(new CommandStompHandler(getFailure(), latch, RemoteCommand.DISCONNECT));

		launcher.setOperation(operation("leave"));
		launcher.setLatch(latch);
		launcher.launch().validate();
		
		assertNull(roomRepository.get(new Room(roomName1)));
	}

	

}
