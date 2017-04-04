package org.bol.game.portal;

import org.bol.game.portal.dto.Command;

/**
 * 
 * It is a layer of abstraction that executes an operation. 
 * Interface to be implemented by any object that wishes to notify a subscriber with Command and payload.
 * 
 * @author VF85400
 *
 * @param <T> A payload of command, at most this is a message, but can be any DTO 
 */
public interface Launcher<T> {

	void launch();
	
	Command<T> getCommand();
}
