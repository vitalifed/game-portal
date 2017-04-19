package org.steelrat.game.portal.mancala.dto;

import org.steelrat.game.portal.dto.User;

/**
 * It doesn't bring anything new besides a class dedicated to mancala game portal.
 * It is here for the future possible extension.
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
 */
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
