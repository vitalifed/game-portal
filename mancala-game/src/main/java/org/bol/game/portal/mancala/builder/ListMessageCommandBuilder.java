package org.bol.game.portal.mancala.builder;

import org.bol.game.portal.CommandBuilder;
import org.bol.game.portal.mancala.launcher.LauncherCollection;
import org.bol.game.portal.mancala.launcher.SimpleLauncher;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

@SuppressWarnings("rawtypes")
public class ListMessageCommandBuilder implements CommandBuilder<LauncherCollection<SimpleLauncher<?>>> {

	private static final String ERROR_MESSAGE = "It's wrapper over list of launchers";

	private LauncherCollection<SimpleLauncher<?>> launcher = new LauncherCollection<>();

	@Override
	public CommandBuilder<LauncherCollection<SimpleLauncher<?>>> operation(SimpMessageSendingOperations operation) {
		launcher.getLaunchers().forEach(l -> l.setOperation(operation));

		return this;
	}

	@Override
	public CommandBuilder<LauncherCollection<SimpleLauncher<?>>> topic(String topic) {
		throw new UnsupportedOperationException(ERROR_MESSAGE);
	}

	@Override
	public CommandBuilder<LauncherCollection<SimpleLauncher<?>>> command(String command) {
		throw new UnsupportedOperationException(ERROR_MESSAGE);
	}

	public CommandBuilder<LauncherCollection<SimpleLauncher<?>>> addLauncher(SimpleLauncher launcherArg) {
		launcher.addLauncher(launcherArg);
		return this;
	}

	public CommandBuilder<LauncherCollection<SimpleLauncher<?>>> insertLauncher(SimpleLauncher launcherArg, int idx) {
		launcher.insertLauncher(launcherArg, idx);
		return this;
	}

	@Override
	public LauncherCollection<SimpleLauncher<?>> build() {
		return launcher;
	}

	public CommandBuilder<LauncherCollection<SimpleLauncher<?>>> message(String messageArg) {
		throw new UnsupportedOperationException(ERROR_MESSAGE);
	}

}
