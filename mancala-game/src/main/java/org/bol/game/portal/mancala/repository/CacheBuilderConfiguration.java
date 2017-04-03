package org.bol.game.portal.mancala.repository;


@FunctionalInterface
public interface CacheBuilderConfiguration<A, R> {
	 public R apply (A a);
}
