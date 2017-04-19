package org.steelrat.game.portal.mancala.dto;

import java.util.HashMap;
import java.util.Map;

import org.steelrat.game.portal.dto.Game;

/**
 * In contrast with {@link Game} given class keeps the user's states
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
 *
 */
public class Mancala extends Game<MancalaUser> {

	private Map<MancalaUser, State> state = new HashMap<>();

	public Map<MancalaUser, State> getState() {
		return state;
	}

	public void setStates(Map<MancalaUser, State> states) {
		this.state = states;
	}

}
