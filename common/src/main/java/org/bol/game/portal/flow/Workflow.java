package org.bol.game.portal.flow;

import org.bol.game.portal.CommandBuilder;
import org.bol.game.portal.dto.User;

public interface Workflow<ActualUser extends User> {

	CommandBuilder<?> createRoom(String room, ActualUser user);
	
	CommandBuilder<?> leaveRoom(String room, ActualUser user);
	
	CommandBuilder<?> startGame(String room);
	
}