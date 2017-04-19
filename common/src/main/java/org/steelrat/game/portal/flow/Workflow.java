package org.steelrat.game.portal.flow;

import org.steelrat.game.portal.LauncherBuilder;
import org.steelrat.game.portal.dto.Game;
import org.steelrat.game.portal.dto.User;

/**
 * Workflow performs a bunch of steps across a particular operation and
 * provides a {@link LauncherBuilder} that enables a controller to dispatch a
 * message to a client.
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
 *
 * @param <ActualUser>
 * @param <ActualGame>
 */
public interface Workflow<ActualUser extends User, ActualGame extends Game<ActualUser>> {

	
	LauncherBuilder<?> createRoom(String room, ActualUser user);

	LauncherBuilder<?> leaveRoom(String room, ActualUser user);

	LauncherBuilder<?> startGame(String room);

	LauncherBuilder<?> stopGame(String room, ActualGame game);

}