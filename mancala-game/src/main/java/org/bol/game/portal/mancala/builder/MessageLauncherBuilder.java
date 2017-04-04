package org.bol.game.portal.mancala.builder;

import org.bol.game.portal.LauncherBuilder;
import org.bol.game.portal.dto.Command;
import org.bol.game.portal.dto.Message;
import org.bol.game.portal.mancala.launcher.SimpleLauncher;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

/**
 * Quite simple builder that makes a {@link SimpleLauncher} with {@link Message}
 * payload injected into {@link Command}
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
 *
 */
public class MessageLauncherBuilder implements LauncherBuilder<SimpleLauncher<Message>> {

	private SimpleLauncher<Message> launcher = new SimpleLauncher<>();

	@Override
	public LauncherBuilder<SimpleLauncher<Message>> operation(SimpMessageSendingOperations operation) {
		launcher.setOperation(operation);
		return this;
	}

	@Override
	public LauncherBuilder<SimpleLauncher<Message>> topic(String topic) {
		launcher.setTopic(topic);
		return this;
	}

	@Override
	public LauncherBuilder<SimpleLauncher<Message>> command(String command) {
		launcher.setCommand(new Command<>(command));
		return this;
	}

	@Override
	public SimpleLauncher<Message> build() {
		return launcher;
	}

	public LauncherBuilder<SimpleLauncher<Message>> message(String messageArg) {
		launcher.setPayload(new Message(messageArg));
		return this;
	}

	public LauncherBuilder<SimpleLauncher<Message>> message(String messageArg, Message.Type type) {
		launcher.setPayload(new Message(messageArg, type));
		return this;
	}

}
