package org.bol.game.portal.mancala.flow;

import org.bol.game.portal.CommandBuilder;
import org.bol.game.portal.dto.User;
import org.bol.game.portal.flow.Workflow;

public interface MancalaWorkflow<ActualUser extends User> extends Workflow<ActualUser> {

	CommandBuilder<?> state(String room, ActualUser user, Integer idx);
	
}
