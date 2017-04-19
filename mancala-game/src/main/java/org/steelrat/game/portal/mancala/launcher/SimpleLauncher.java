package org.steelrat.game.portal.mancala.launcher;

import org.steelrat.game.portal.Launcher;
import org.steelrat.game.portal.dto.Command;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

/**
 * Simple launcher, it dispatches a {@link Command} with generic payload <T>
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
 *
 * @param <T> Any DTO
 */
public class SimpleLauncher<T> implements Launcher<T> {

	private SimpMessageSendingOperations operation;
	private String topic;

	private T payload;

	private Command<T> command;

	@Override
	public void launch() {
		command.setPayload(payload);
		operation.convertAndSend(topic, command);
	}

	public void setOperation(SimpMessageSendingOperations operation) {
		this.operation = operation;
	}

	@Override
	public Command<T> getCommand() {
		return command;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public void setPayload(T payload) {
		this.payload = payload;
	}

	public void setCommand(Command<T> command) {
		this.command = command;

	}

}
