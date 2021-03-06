package org.steelrat.game.portal.dto;

/**
 * Typical DTO that works as a container for server to client communication. 
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
 *
 * @param <Payload>
 *            It can be any DTO
 */
public class Command<Payload> {

	private String command;
	private Payload payload;

	public Command() {
	}

	public Command(String command) {
		super();
		this.command = command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}

	public Payload getPayload() {
		return payload;
	}

	public void setPayload(Payload payload) {
		this.payload = payload;
	}

	@Override
	public String toString() {
		return "Command [command=" + command + ", payload=" + payload + "]";
	}

}
