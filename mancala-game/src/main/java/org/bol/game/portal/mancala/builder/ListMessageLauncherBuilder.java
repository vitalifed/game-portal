package org.bol.game.portal.mancala.builder;

import org.bol.game.portal.LauncherBuilder;
import org.bol.game.portal.mancala.launcher.LauncherCollection;
import org.bol.game.portal.mancala.launcher.SimpleLauncher;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

/**
 * This is an implementation of a builder that puts into a stack list of launchers, 
 * where all of them will be executed one by one at once.
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
 *
 */
@SuppressWarnings("rawtypes")
public class ListMessageLauncherBuilder implements LauncherBuilder<LauncherCollection<SimpleLauncher<?>>> {

	private static final String ERROR_MESSAGE = "It's wrapper over list of launchers";

	private LauncherCollection<SimpleLauncher<?>> launcher = new LauncherCollection<>();

	@Override
	public LauncherBuilder<LauncherCollection<SimpleLauncher<?>>> operation(SimpMessageSendingOperations operation) {
		launcher.getLaunchers().forEach(l -> l.setOperation(operation));

		return this;
	}

	@Override
	public LauncherBuilder<LauncherCollection<SimpleLauncher<?>>> topic(String topic) {
		throw new UnsupportedOperationException(ERROR_MESSAGE);
	}

	@Override
	public LauncherBuilder<LauncherCollection<SimpleLauncher<?>>> command(String command) {
		throw new UnsupportedOperationException(ERROR_MESSAGE);
	}

	public LauncherBuilder<LauncherCollection<SimpleLauncher<?>>> addLauncher(SimpleLauncher launcherArg) {
		launcher.addLauncher(launcherArg);
		return this;
	}

	public LauncherBuilder<LauncherCollection<SimpleLauncher<?>>> insertLauncher(SimpleLauncher launcherArg, int idx) {
		launcher.insertLauncher(launcherArg, idx);
		return this;
	}

	@Override
	public LauncherCollection<SimpleLauncher<?>> build() {
		return launcher;
	}

	public LauncherBuilder<LauncherCollection<SimpleLauncher<?>>> message(String messageArg) {
		throw new UnsupportedOperationException(ERROR_MESSAGE);
	}

}
