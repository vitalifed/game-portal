package org.steelrat.game.portal.mancala.dto;

import java.util.Arrays;

/**
 * The class represents a current state of game belonging to particular user
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
 *
 */
public class State {

	private int[] currentState = {6,6,6,6,6,6};
	private int score;
	
	public int[] getState() {
		return currentState;
	}
	
	public void setState(int[] state) {
		this.currentState = state;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "State [state=" + Arrays.toString(currentState) + ", score=" + score + "]";
	}
	
	
}
