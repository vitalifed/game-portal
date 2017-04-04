package org.bol.game.portal.mancala.builder;

import org.bol.game.portal.LauncherBuilder;
import org.bol.game.portal.dto.Command;
import org.bol.game.portal.mancala.dto.State;
import org.bol.game.portal.mancala.launcher.SimpleLauncher;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.List;

/**
 * In contrast with {@link MessageLauncherBuilder}, it builds a
 * {@link SimpleLauncher}, where a luncher dispatches to a client a list of
 * states wrapped by {@link Command}
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
 *
 */
public class StateLauncherBuilder implements LauncherBuilder<SimpleLauncher<List<State>>> {

	private SimpleLauncher<List<State>> launcher = new SimpleLauncher<List<State>>();

	@Override
	public LauncherBuilder<SimpleLauncher<List<State>>> operation(SimpMessageSendingOperations operation) {
		launcher.setOperation(operation);
		return this;
	}

	@Override
	public LauncherBuilder<SimpleLauncher<List<State>>> topic(String topic) {
		launcher.setTopic(topic);
		return this;
	}

	@Override
	public LauncherBuilder<SimpleLauncher<List<State>>> command(String command) {
		launcher.setCommand(new Command<>(command));
		return this;
	}

	@Override
	public SimpleLauncher<List<State>> build() {
		return launcher;
	}

	public LauncherBuilder<SimpleLauncher<List<State>>> payload(List<State> states) {
		launcher.setPayload(states);
		return this;
	}

}
