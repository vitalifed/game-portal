package org.bol.game.portal.mancala.intg.builder;

@FunctionalInterface
public interface Validation<A, B, R> {
	 public R apply (A a, B b) throws Exception;
}
