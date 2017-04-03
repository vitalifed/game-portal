package org.bol.game.portal.mancala.flow;

public enum RemoteCommand {

	SUBSCRIBE("subscribe"), REFUSE("refuse"), DISCONNECT("disconnect"), START("start"), STATE("state"), MOVE("move"), STOP("stop");
	
	private String cmd;
	
	private RemoteCommand(String cmd) {
		this.cmd = cmd;
	}
	
	public String command(){
		return cmd;
	}
	
}
