package org.bol.game.portal.mancala.launcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bol.game.portal.Launcher;
import org.bol.game.portal.dto.Command;

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
