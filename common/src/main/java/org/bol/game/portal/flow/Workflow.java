package org.bol.game.portal.flow;

import org.bol.game.portal.CommandBuilder;
import org.bol.game.portal.dto.Game;
import org.bol.game.portal.dto.User;

/**
 * Workflows performs a bunch of steps across a particular operation and
 * provides a {@link CommandBuilder} that enables a controller to dispatch a
 * message to client.
 * 
 * @author VF85400
 *
 * @param <ActualUser>
 * @param <ActualGame>
 */
public interface Workflow<ActualUser extends User, ActualGame extends Game<ActualUser>> {

	/**
	 * @param room Room's name
	 * @param user User
	 * @return Command builder builder 
	 */
	CommandBuilder<?> createRoom(String room, ActualUser user);

	CommandBuilder<?> leaveRoom(String room, ActualUser user);

	CommandBuilder<?> startGame(String room);

	CommandBuilder<?> stopGame(String room, ActualGame game);

}