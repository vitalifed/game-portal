package org.bol.game.portal.repository;

import org.bol.game.portal.dto.Game;
import org.bol.game.portal.dto.Room;
import org.bol.game.portal.dto.User;
import org.bol.game.portal.exception.GamePortalException;

/**
 * The interface provides an abstraction over data manipulation, it unifies the
 * communications of controllers and data source, whatever it is.
 * 
 * @author VF85400
 *
 * @param <ActualUser>
 *            Actual implementation of User
 * @param <ActualGame>
 *            Actual implementation of Game
 */
public interface RoomRepository<ActualUser extends User, ActualGame extends Game<ActualUser>> {

	public ActualGame get(Room room);

	public int addUser(Room room, ActualUser user) throws GamePortalException;

	public ActualUser[] removeUser(Room room, ActualUser user);

	void removeRoom(Room room);

	void init(Room room);
}
