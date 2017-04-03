package org.bol.game.portal.repository;

import org.bol.game.portal.dto.Game;
import org.bol.game.portal.dto.Room;
import org.bol.game.portal.dto.User;
import org.bol.game.portal.exception.GamePortalException;
import org.bol.game.portal.exception.RoomOverflowException;

public interface RoomRepository<ActualUser extends User, ActualGame extends Game<ActualUser>> {

	public ActualGame get(Room room);

	public int addUser(Room room, ActualUser user) throws GamePortalException;
	
	public ActualUser[] removeUser(Room room, ActualUser user);

	void removeRoom(Room room);
	
	void startGame(Room room);
}
