package org.steelrat.game.portal.mancala.repository;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.steelrat.game.portal.dto.Room;
import org.steelrat.game.portal.exception.GamePortalException;
import org.steelrat.game.portal.exception.RoomOverflowException;
import org.steelrat.game.portal.exception.RoomsThresholdExceedException;
import org.steelrat.game.portal.mancala.dto.Mancala;
import org.steelrat.game.portal.mancala.dto.MancalaUser;
import org.junit.Test;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public class MancalaRoomRepositoryTest {

	@Test(expected = RoomsThresholdExceedException.class)
	public void testExceedThreshold() throws GamePortalException {
		MancalaRoomRepository rep = new MancalaRoomRepository(repositoryConfiguration(null, 10, TimeUnit.MINUTES));
		for (int i = 0; i < 200; i++) {
			rep.addUser(new Room("room" + (i+1000)), new MancalaUser("user" + i));
		}
	}

	@Test(expected = RoomOverflowException.class)
	public void testExceptionOverflow() throws GamePortalException {
		MancalaRoomRepository rep = new MancalaRoomRepository(repositoryConfiguration(null, 10, TimeUnit.MINUTES));
		rep.addUser(new Room("room"), new MancalaUser("user", "1"));
		rep.addUser(new Room("room"), new MancalaUser("user", "2"));
		rep.addUser(new Room("room"), new MancalaUser("user", "3"));
	}
	
	@Test
	public void testExpiration() throws GamePortalException, InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		
		MancalaRoomRepository rep = new MancalaRoomRepository(repositoryConfiguration(listener(latch), 5, TimeUnit.SECONDS));
		rep.addUser(new Room("room"), new MancalaUser("user", "1"));
		rep.addUser(new Room("room"), new MancalaUser("user", "2"));
		
		Thread.sleep(15000);
		
		assertNull(rep.get(new Room("room")));
		
		if (!latch.await(10, TimeUnit.SECONDS)){
			fail("Cache is not cleaned up");
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	private CacheBuilderConfiguration<Integer, CacheBuilder> repositoryConfiguration(
			RemovalListener<Room, Mancala> listener, int time, TimeUnit timeunit) {
		return (threshold) -> {
			Function<CacheBuilder<Object, Object>, CacheBuilder<Object, Object>> consumer = builder -> {
				if (listener != null)
					builder.removalListener(listener);

				return builder;
			};
			return consumer.apply(CacheBuilder.newBuilder().concurrencyLevel(4).maximumSize(threshold)
					.expireAfterAccess(time, timeunit));
		};
	}

	private RemovalListener<Room, Mancala> listener(CountDownLatch latch) {
		return new RemovalListener<Room, Mancala>() {
			@Override
			public void onRemoval(RemovalNotification<Room, Mancala> notification) {
				System.out.println("Removed");
				latch.countDown();
			}
		};
	}
}
