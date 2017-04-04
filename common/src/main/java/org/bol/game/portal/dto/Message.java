package org.bol.game.portal.dto;

public class Message {

	public enum Type {
		WARNING, INFO
	}

	private String message;
	private Type type = Type.INFO;

	public Message() {
	}

	public Message(String message) {
		this.message = message;
	}

	public Message(String message, Type type) {
		this.message = message;
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Message [message=" + message + ", type=" + type + "]";
	}

}
