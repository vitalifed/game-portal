package org.bol.game.portal.mancala.repository;

import org.bol.game.portal.dto.Room;
import org.bol.game.portal.exception.GamePortalException;
import org.bol.game.portal.exception.RoomOverflowException;
import org.bol.game.portal.exception.RoomsThresholdExceedException;
import org.bol.game.portal.mancala.dto.MancalaUser;
import org.junit.Test;

public class MancalaRoomRepositoryTest {

	@Test(expected = RoomsThresholdExceedException.class)
	public void testExceedThreshold() throws GamePortalException {
		MancalaRoomRepository rep = new MancalaRoomRepository();
		for (int i = 0; i < 200; i++) {
			rep.addUser(new Room("room" + (i+1000)), new MancalaUser("user" + i));
		}
	}

	@Test(expected = RoomOverflowException.class)
	public void testExceptionOverflow() throws GamePortalException {
		MancalaRoomRepository rep = new MancalaRoomRepository();
		rep.addUser(new Room("room"), new MancalaUser("user", "1"));
		rep.addUser(new Room("room"), new MancalaUser("user", "2"));
		rep.addUser(new Room("room"), new MancalaUser("user", "3"));
	}

}
