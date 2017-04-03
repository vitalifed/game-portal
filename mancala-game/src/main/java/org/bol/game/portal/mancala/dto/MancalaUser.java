package org.bol.game.portal.mancala.dto;

import org.bol.game.portal.dto.User;

public class MancalaUser extends User {

	public MancalaUser() {
		super();
	}

	public MancalaUser(String name) {
		super(name);
	}

	public MancalaUser(String name, String token) {
		this(name);
		setToken(token);
	}

}
