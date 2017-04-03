package org.bol.game.portal.dto;

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
