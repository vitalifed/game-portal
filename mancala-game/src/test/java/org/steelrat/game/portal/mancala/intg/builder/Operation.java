package org.steelrat.game.portal.mancala.intg.builder;

@FunctionalInterface
public interface Operation<A, B, C, D, R> {
	 public R apply (A a, B b, C c, D d) throws Exception;
}
