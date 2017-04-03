package org.bol.game.portal.mancala.flow.util;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.bol.game.portal.mancala.dto.State;
import org.junit.Test;

public class RefineStateTest {

	private State[] init(int[] src){
		State[] states = new State[2];
		states[0] = new State();
		states[1] = new State();
		
		System.arraycopy(src, 0, states[0].getState(), 0, 6);
		System.arraycopy(src, 7, states[1].getState(), 0, 6);
		
		ArrayUtils.reverse(states[1].getState());
		
		states[0].setScore(src[6]);		
		states[1].setScore(src[13]);	
		
		return states;
	}
	
	private void compareResults(State[] states, int[] res){
		int[] dest = new int[14];
		
		System.arraycopy(states[0].getState(), 0, dest, 0, 6);
		ArrayUtils.reverse(states[1].getState());
		System.arraycopy(states[1].getState(), 0, dest, 7, 6);
		
		dest[6] = states[0].getScore();
		dest[13] = states[1].getScore();
		
		assertTrue(Arrays.equals(res, dest));
	}
	
	@Test
	public void testMoveToZero1() {
		int start = 0;
		int[] src = {7, 0,  9,  9, 0,  9, 3, 0, 8, 7, 7, 7, 1, 2};
		int[] res = {0, 1, 10, 10, 1,  0, 15, 0, 8, 7, 7, 7, 1, 2};

		State[] states = init(src);
		
		assertFalse(RefineState.refine(states, start));
		assertEquals(res[6], states[0].getScore());
		assertEquals(res[13], states[1].getScore());
		compareResults(states, res);
		
		System.out.println(Arrays.toString(src));
		int idx = RefineState.move(src, start);
		System.out.println(Arrays.toString(src));
		System.out.println(Arrays.toString(res));
		
		assertEquals(7, idx);
		assertTrue(Arrays.equals(res, src));
		
	}
	
	@Test
	public void testMoveToZero2() {
		int start = 0;
		int[] src = {1, 0, 9, 9, 1, 9, 3, 0, 8, 7, 7, 0, 7, 2};
		int[] res = {0, 1, 9, 9, 1, 9, 3, 0, 8, 7, 7, 0, 7, 2};
		
		State[] states = init(src);
		
		assertFalse(RefineState.refine(states, start));
		assertEquals(res[6], states[0].getScore());
		assertEquals(res[13], states[1].getScore());
		compareResults(states, res);
		
		System.out.println();
		
		System.out.println(Arrays.toString(src));
		int idx = RefineState.move(src, start);
		System.out.println(Arrays.toString(src));
		System.out.println(Arrays.toString(res));
		
		assertEquals(1, idx);
		assertTrue(Arrays.equals(res, src));
	}

	
	@Test
	public void testMoveToOneStep() {
		int[] src = {1, 0, 9, 9, 1, 9, 3, 0, 8, 7, 7, 0, 7, 2};
		int[] res = {1, 0, 9, 9, 0, 10, 3, 0, 8, 7, 7, 0, 7, 2};
		
		State[] states = init(src);
		
		assertFalse(RefineState.refine(states, 4));
		assertEquals(res[6], states[0].getScore());
		assertEquals(res[13], states[1].getScore());
		compareResults(states, res);
		System.out.println();
		
		System.out.println(Arrays.toString(src));
		int idx = RefineState.move(src, 4);
		System.out.println(Arrays.toString(src));
		System.out.println(Arrays.toString(res));
		
		assertEquals(5, idx);
		assertTrue(Arrays.equals(res, src));
	}
	
	@Test
	public void testMoveBigStep() {
		int[] src = {1, 0, 9, 9, 1, 9, 3, 0, 8, 7, 7, 0, 7, 2};
		int[] res = {1, 0, 0, 10, 2, 10, 4, 1, 9, 8, 8, 1, 7, 2};
		
		State[] states = init(src);
		
		assertFalse(RefineState.refine(states, 2));
		assertEquals(res[6], states[0].getScore());
		assertEquals(res[13], states[1].getScore());
		compareResults(states, res);
		System.out.println();
		
		System.out.println(Arrays.toString(src));
		int idx = RefineState.move(src, 2);
		System.out.println(Arrays.toString(src));
		System.out.println(Arrays.toString(res));
		
		assertEquals(11, idx);
		assertTrue(Arrays.equals(res, src));
	}
	
	@Test
	public void testMoveToScore() {
		int[] src = {1, 0, 9, 3, 1, 9, 3, 0, 8, 7, 7, 0, 7, 2};
		int[] res = {1, 0, 9, 0, 2, 10, 4, 0, 8, 7, 7, 0, 7, 2};
		
		State[] states = init(src);
		
		assertTrue(RefineState.refine(states, 3));
		assertEquals(res[6], states[0].getScore());
		assertEquals(res[13], states[1].getScore());
		compareResults(states, res);
		
		System.out.println();
		
		System.out.println(Arrays.toString(src));
		int idx = RefineState.move(src, 3);
		System.out.println(Arrays.toString(src));
		System.out.println(Arrays.toString(res));
		
		assertEquals(6, idx);
		assertTrue(Arrays.equals(res, src));
	}
	
	@Test
	public void testMoveToOppScore() {
		int[] src = {13, 0, 9,  3, 1, 9, 3, 0, 8, 7, 7, 0, 7, 2};
		int[] res = {0, 1, 10,  4, 2, 10, 4, 1, 9, 8, 8, 1, 8, 3};
		
		State[] states = init(src);
		
		assertFalse(RefineState.refine(states, 0));
		assertEquals(res[6], states[0].getScore());
		assertEquals(res[13], states[1].getScore());
		compareResults(states, res);
		
		System.out.println();
		
		System.out.println(Arrays.toString(src));
		int idx = RefineState.move(src, 0);
		System.out.println(Arrays.toString(src));
		System.out.println(Arrays.toString(res));
		
		assertEquals(13, idx);
		assertTrue(Arrays.equals(res, src));
	}
	
	@Test
	public void testMoveToZeroOppScore() {
		int[] src = {13, 0, 9,  3, 1, 9, 3, 0, 8, 7, 7, 0, 7, 0};
		int[] res = {0, 1, 10,  4, 2, 10, 4, 1, 9, 8, 8, 1, 8, 1};
		
		State[] states = init(src);
		
		assertFalse(RefineState.refine(states, 0));
		assertEquals(res[6], states[0].getScore());
		assertEquals(res[13], states[1].getScore());
		compareResults(states, res);
		
		System.out.println();
		
		System.out.println(Arrays.toString(src));
		int idx = RefineState.move(src, 0);
		System.out.println(Arrays.toString(src));
		System.out.println(Arrays.toString(res));
		
		assertEquals(13, idx);
		assertTrue(Arrays.equals(res, src));
	}
	
	@Test
	public void testMoveToZeroPlayerScore() {
		int[] src = {13, 0, 9, 3, 1, 9, 0, 0, 8, 7, 7, 0, 7, 0};
		int[] res = {13, 0, 9, 0, 2, 10, 1, 0, 8, 7, 7, 0, 7, 0};
		
		State[] states = init(src);
		
		assertTrue(RefineState.refine(states, 3));
		assertEquals(res[6], states[0].getScore());
		assertEquals(res[13], states[1].getScore());
		compareResults(states, res);
		
		System.out.println();
		
		System.out.println(Arrays.toString(src));
		int idx = RefineState.move(src, 3);
		System.out.println(Arrays.toString(src));
		System.out.println(Arrays.toString(res));
		
		assertEquals(6, idx);
		assertTrue(Arrays.equals(res, src));
	}

	@Test
	public void testMoveCycle() {
		int[] src = {20, 0, 9, 3, 1, 9,  5, 0, 8, 7, 7, 0, 7, 0};
		int[] res = {1, 2, 11, 5, 3, 11, 7, 1, 9, 8, 8, 1, 8, 1};
		
		State[] states = init(src);
		
		assertTrue(RefineState.refine(states, 0));
		assertEquals(res[6], states[0].getScore());
		assertEquals(res[13], states[1].getScore());
		compareResults(states, res);
		
		System.out.println();
		
		System.out.println(Arrays.toString(src));
		int idx = RefineState.move(src, 0);
		System.out.println(Arrays.toString(src));
		System.out.println(Arrays.toString(res));
		
		assertEquals(6, idx);
		assertTrue(Arrays.equals(res, src));
	}

}
