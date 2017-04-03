package org.bol.game.portal.mancala.launcher;

import java.util.List;

import org.bol.game.portal.Launcher;
import org.bol.game.portal.dto.Command;
import org.bol.game.portal.mancala.dto.State;

public class StateLauncher implements Launcher<List<State>> {

	@Override
	public void launch() {
	}

	@Override
	public Command<List<State>> getCommand() {
		return null;
	}

}
