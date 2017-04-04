package org.bol.game.portal.mancala.repository;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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