package org.steelrat.game.portal.mancala.repository;

import org.steelrat.game.portal.mancala.config.MancalaConfiguration;

/**
 * Developed specifically to move configuration complexity to a place that's explicit and easier to control.
 * 
 * {@link MancalaConfiguration}
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
 *
 * @param <A> Input parameter
 * @param <R> Output value
 */
@FunctionalInterface
public interface CacheBuilderConfiguration<A, R> {
	 public R apply (A a);
}
