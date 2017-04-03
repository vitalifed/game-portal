package org.bol.game.portal.mancala.dto;

import java.util.Arrays;

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
