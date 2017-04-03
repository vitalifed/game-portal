package org.bol.game.portal;

import org.bol.game.portal.dto.Command;

public interface Launcher<T> {

	void launch();
	
	Command<T> getCommand();
}
