package org.steelrat.game.portal;

import org.steelrat.game.portal.dto.Command;

/**
 * 
 * It is a layer of abstraction that executes an operation. 
 * Interface to be implemented by any object that wishes to notify a subscriber with Command and payload.
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
 *
 * @param <T> A payload of command, at most this is a message, but can be any DTO 
 */
public interface Launcher<T> {

	void launch();
	
	Command<T> getCommand();
}
