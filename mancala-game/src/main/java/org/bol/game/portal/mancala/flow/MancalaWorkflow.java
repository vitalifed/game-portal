package org.bol.game.portal.mancala.flow;

import org.bol.game.portal.CommandBuilder;
import org.bol.game.portal.dto.User;

public interface MancalaWorkflow<ActualUser extends User> {

	CommandBuilder<?> state(String room, ActualUser user, Integer idx);
	
}
