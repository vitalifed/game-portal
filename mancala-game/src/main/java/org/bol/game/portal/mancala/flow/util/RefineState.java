package org.bol.game.portal.mancala.flow.util;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.bol.game.portal.mancala.dto.State;

public class RefineState {

	private RefineState() {
	    throw new IllegalAccessError("Utility class");
	  }

	public static boolean refine(State[] states, int idx) {

		int[] dest = new int[14];

		System.arraycopy(states[0].getState(), 0, dest, 0, 6);
		ArrayUtils.reverse(states[1].getState());
		System.arraycopy(states[1].getState(), 0, dest, 7, 6);

		dest[6] = states[0].getScore();
		dest[13] = states[1].getScore();

		idx = move(dest, idx);
		
		System.arraycopy(dest, 0, states[0].getState(), 0, 6);
		System.arraycopy(dest, 7, states[1].getState(), 0, 6);

		ArrayUtils.reverse(states[1].getState());

		states[0].setScore(dest[6]);
		states[1].setScore(dest[13]);

		return idx == 6;
	}

	public static int move(int[] src, int idx) {

		// -1< idx < 6

		int val = src[idx];
		if (val == 0)
			return idx;

		src[idx] = 0;
		idx++;
		while (val > 0) {
			val--;

			if (idx == src.length) {
				idx = 0;
			}

			if (val == 0 && idx != 13 && idx != 6 && src[idx] == 0 && src[12 - idx]>0) {
				src[6] = src[6] + 1 + src[12 - idx];
				src[12 - idx] = 0;
				idx++;
			} else
				src[idx++]++;
		}

		return --idx;
	}

	public static void finishGame(State[] states){
		Arrays.stream(states).forEach(state->finishState(state));
	}
	
	private static void finishState(State state){
		state.setScore(state.getScore()+Arrays.stream(state.getState()).sum());
		state.setState(new int[6]);
	}
	
	
	
	
}
