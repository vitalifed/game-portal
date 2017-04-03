package org.bol.game.portal.mancala.dto;

import java.util.HashMap;
import java.util.Map;

import org.bol.game.portal.dto.Game;

public class Mancala extends Game<MancalaUser> {

	private Map<MancalaUser, State> state = new HashMap<>();

	public Map<MancalaUser, State> getState() {
		return state;
	}

	public void setStates(Map<MancalaUser, State> states) {
		this.state = states;
	}

}
