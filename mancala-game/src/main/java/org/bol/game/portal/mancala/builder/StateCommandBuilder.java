package org.bol.game.portal.mancala.builder;

import org.bol.game.portal.CommandBuilder;
import org.bol.game.portal.dto.Command;
import org.bol.game.portal.mancala.dto.State;
import org.bol.game.portal.mancala.launcher.SimpleLauncher;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.List;

public class StateCommandBuilder implements CommandBuilder<SimpleLauncher<List<State>>> {
	
	private SimpleLauncher<List<State>> launcher = new SimpleLauncher<List<State>>(); 
	
	@Override
	public CommandBuilder<SimpleLauncher<List<State>>> operation(SimpMessageSendingOperations operation) {
		launcher.setOperation(operation);
		return this;
	}

	@Override
	public CommandBuilder<SimpleLauncher<List<State>>> topic(String topic) {
		launcher.setTopic(topic);
		return this;
	}

	@Override
	public CommandBuilder<SimpleLauncher<List<State>>> command(String command) {
		launcher.setCommand(new Command<>(command));
		return this;
	}
	
	@Override
	public SimpleLauncher<List<State>> build() {
		return launcher;
	}

	public CommandBuilder<SimpleLauncher<List<State>>> payload(List<State> states){
		launcher.setPayload(states);
		return this;
	}
	
}
