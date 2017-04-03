package org.bol.game.portal.mancala.builder;

import org.bol.game.portal.CommandBuilder;
import org.bol.game.portal.dto.Command;
import org.bol.game.portal.dto.Message;
import org.bol.game.portal.mancala.launcher.SimpleLauncher;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

public class MessageCommandBuilder implements CommandBuilder<SimpleLauncher<Message>> {
	
	private SimpleLauncher<Message> launcher = new SimpleLauncher<>(); 
	
	@Override
	public CommandBuilder<SimpleLauncher<Message>> operation(SimpMessageSendingOperations operation) {
		launcher.setOperation(operation);
		return this;
	}

	@Override
	public CommandBuilder<SimpleLauncher<Message>> topic(String topic) {
		launcher.setTopic(topic);
		return this;
	}

	@Override
	public CommandBuilder<SimpleLauncher<Message>> command(String command) {
		launcher.setCommand(new Command<>(command));
		return this;
	}
	
	@Override
	public SimpleLauncher<Message> build() {
		return launcher;
	}

	public CommandBuilder<SimpleLauncher<Message>> message(String messageArg){
		launcher.setPayload(new Message(messageArg));
		return this;
	}
	
	public CommandBuilder<SimpleLauncher<Message>> message(String messageArg, Message.Type type){
		launcher.setPayload(new Message(messageArg, type));
		return this;
	}
	
}
