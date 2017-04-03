package org.bol.game.portal;

import org.bol.game.portal.dto.Command;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

/**
 * This is a generic command builder, an intention to set up and build a
 * launcher that sends a particular command with payload.<br>
 * {@link Command}<br>
 * {@link Launcher}
 * 
 * @author VF85400
 *
 * @param <T> extends {@link Launcher}
 */
public interface CommandBuilder<T extends Launcher<?>> {

	CommandBuilder<T> operation(SimpMessageSendingOperations operation);

	CommandBuilder<T> topic(String topic);

	CommandBuilder<T> command(String command);

	T build();
}
