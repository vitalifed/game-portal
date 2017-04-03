package org.bol.game.portal.mancala.repository;

import static org.junit.Assert.assertNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.bol.game.portal.dto.Room;
import org.bol.game.portal.exception.GamePortalException;
import org.bol.game.portal.exception.RoomOverflowException;
import org.bol.game.portal.exception.RoomsThresholdExceedException;
import org.bol.game.portal.mancala.dto.Mancala;
import org.bol.game.portal.mancala.dto.MancalaUser;
import org.bol.game.portal.mancala.flow.GameMancalaWorkflow;
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
		MancalaRoomRepository rep = new MancalaRoomRepository(repositoryConfiguration(listener(), 5, TimeUnit.SECONDS));
		rep.addUser(new Room("room"), new MancalaUser("user", "1"));
		rep.addUser(new Room("room"), new MancalaUser("user", "2"));
		
		Thread.sleep(10000);
		
		assertNull(rep.get(new Room("room")));
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

	private RemovalListener<Room, Mancala> listener() {
		return new RemovalListener<Room, Mancala>() {
			@Override
			public void onRemoval(RemovalNotification<Room, Mancala> notification) {
				System.out.println("Removed");
			}
		};
	}
}
