package org.bol.game.portal.mancala.repository;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Caches built with CacheBuilder do not perform cleanup and evict values
 * "automatically," or instantly after a value expires, or anything of the sort.
 * Instead, it performs small amounts of maintenance during write operations, or
 * during occasional read operations
 * 
 * In order to schedule regular cache maintenance given implementation just
 * schedules the maintenance using ScheduledExecutorService..
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
 *
 */
class CleanUpControl {
	private final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public static void cleanUp(final MancalaRoomRepository roomRepository) {
		final Runnable cleaner = new Runnable() {
			public void run() {
				roomRepository.cleanUp();
			}
		};
		scheduler.scheduleAtFixedRate(cleaner, 10, 10, TimeUnit.SECONDS);
	}
}