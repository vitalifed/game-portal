package org.bol.game.portal.mancala.launcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bol.game.portal.Launcher;
import org.bol.game.portal.dto.Command;

/**
 * This is an implementation of a decorator pattern, it is designed so that
 * multiple Launcher's can be stacked in a list and executed one by one.
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
 *
 * @param <T> One of {@link Launcher} implementation
 */
public class LauncherCollection<T extends Launcher<?>> implements Launcher<T> {

	private List<T> launchers = new ArrayList<>();

	@Override
	public void launch() {
		launchers.forEach(l -> l.launch());
	}

	@Override
	public Command<T> getCommand() {
		throw new UnsupportedOperationException(
				"The launcher enterly responsible for starting list of launchers. It has no its own command");
	}

	public void addLauncher(T launcher) {
		launchers.add(launcher);
	}

	public void insertLauncher(T launcher, int idx) {
		launchers.add(idx, launcher);
	}

	public List<T> getLaunchers() {
		return Collections.unmodifiableList(launchers);
	}

}
