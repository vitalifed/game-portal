package org.bol.game.portal.mancala.flow;

import static org.junit.Assert.*;

import java.util.Map;

import org.bol.game.portal.CommandBuilder;
import org.bol.game.portal.Launcher;
import org.bol.game.portal.dto.Room;
import org.bol.game.portal.mancala.dto.MancalaUser;
import org.bol.game.portal.mancala.dto.State;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GameMancalaWorkflowTest {

	@Autowired
	@Qualifier("workflow")
	private GameMancalaWorkflow workflow;

	@Configuration
	@ComponentScan("org.bol.game.portal.mancala")
	@PropertySource("classpath:application.properties")
	static class ContextConfiguration {
	}

	private String room1 = "room1";
	private MancalaUser user1 = new MancalaUser("name1", "token1");
	private MancalaUser user2 = new MancalaUser("name2", "token2");
	private String room2 = "room2";
	private MancalaUser user3 = new MancalaUser("name3", "token3");

	@Test
	public void testStartGame() {
		// workflow.startGame("room");
	}

	@Test
	public void testState() {
	//	fail("Not yet implemented");
	}

	@Test
	public void test1CreateRoom() {
		Launcher<?> launcher = workflow.createRoom(room1, user1).build();
		assertEquals(RemoteCommand.SUBSCRIBE.command(), launcher.getCommand().getCommand());
		Map<MancalaUser, State> state = workflow.getRoomRepository().get(new Room(room1)).getState();
		
		assertEquals(1, workflow.getRoomRepository().get(new Room(room1)).getState().size());
		assertTrue(state.containsKey(user1));
		
		launcher = workflow.createRoom(room1, user2).build();
		//assertEquals(RemoteCommand.SUBSCRIBE.command(), launcher.getCommand().getCommand());
		state = workflow.getRoomRepository().get(new Room(room1)).getState();
		
		assertEquals(2, workflow.getRoomRepository().get(new Room(room1)).getState().size());
		assertTrue(state.containsKey(user1));
		assertTrue(state.containsKey(user2));
		
	}

	@Test
	public void testLeaveRoom() {
	//	fail("Not yet implemented");
	}

}
