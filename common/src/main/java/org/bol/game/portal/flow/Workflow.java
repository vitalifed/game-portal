package org.bol.game.portal.flow;

import org.bol.game.portal.CommandBuilder;
import org.bol.game.portal.dto.Game;
import org.bol.game.portal.dto.User;

public interface Workflow<ActualUser extends User, ActualGame extends Game<ActualUser>> {

	CommandBuilder<?> createRoom(String room, ActualUser user);
	
	CommandBuilder<?> leaveRoom(String room, ActualUser user);
	
	CommandBuilder<?> startGame(String room);
	
	CommandBuilder<?> stopGame(String room, ActualGame game);
		
}