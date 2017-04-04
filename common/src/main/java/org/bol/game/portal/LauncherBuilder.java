package org.bol.game.portal;

import org.bol.game.portal.dto.Command;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

/**
 * This is a generic launcher builder, an intention to set up and build a
 * launcher that sends a particular command with payload.<br>
 * {@link Command}<br>
 * {@link Launcher}
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
 *
 * @param <T> extends {@link Launcher}
 */
public interface LauncherBuilder<T extends Launcher<?>> {

	LauncherBuilder<T> operation(SimpMessageSendingOperations operation);

	LauncherBuilder<T> topic(String topic);

	LauncherBuilder<T> command(String command);

	T build();
}
