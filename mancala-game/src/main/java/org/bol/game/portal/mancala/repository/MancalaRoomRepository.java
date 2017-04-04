package org.bol.game.portal.mancala.repository;

import java.util.Map.Entry;
import java.util.Set;

import org.bol.game.portal.dto.Room;
import org.bol.game.portal.exception.GamePortalException;
import org.bol.game.portal.exception.RoomOverflowException;
import org.bol.game.portal.exception.RoomsThresholdExceedException;
import org.bol.game.portal.mancala.config.MancalaConfiguration;
import org.bol.game.portal.mancala.dto.Mancala;
import org.bol.game.portal.mancala.dto.MancalaUser;
import org.bol.game.portal.mancala.dto.State;
import org.bol.game.portal.repository.RoomRepository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * The class provides a persistence mechanism, where the actual datasource is a
 * cache with a threshold of amounts and eviction policy.
 * 
 * The default threshold equals 100.
 * The cache evicts a value from cache based upon configuration defined in {@link MancalaConfiguration}
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
 *
 */
public class MancalaRoomRepository implements RoomRepository<MancalaUser, Mancala> {

	private static final int ROOMS_AMOUNT_THRESHOLD = 100;

	private int threshold;

	private static Cache<Room, Mancala> roomRepository;

	@SuppressWarnings("rawtypes")
	public MancalaRoomRepository(CacheBuilderConfiguration<Integer, CacheBuilder> builder) {
		this(builder, ROOMS_AMOUNT_THRESHOLD);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MancalaRoomRepository(CacheBuilderConfiguration<Integer, CacheBuilder> configuration, int thresholdArg) {
		this.threshold = thresholdArg;
		roomRepository = configuration.apply(threshold).build();

		CleanUpControl.cleanUp(this);
	}

	public void cleanUp() {
		roomRepository.cleanUp();
	}

	public Mancala get(Room room) {
		return roomRepository.asMap().get(room);
	}

	public synchronized int addUser(Room room, MancalaUser user) throws GamePortalException {
		Mancala mancala = roomRepository.asMap().get(room);
		if (mancala == null) {
			if (roomRepository.size() == threshold) {
				throw new RoomsThresholdExceedException("The amount of the rooms exceeds threshold");
			}
			mancala = new Mancala();
			roomRepository.put(room, mancala);
		}

		if (mancala.getState().size() > 1)
			throw new RoomOverflowException("Room has already 2 players");

		mancala.getState().put(user, null);

		return mancala.getState().size();
	}

	public synchronized MancalaUser[] removeUser(Room room, MancalaUser user) {
		Mancala mancala = roomRepository.asMap().get(room);
		if (mancala != null)
			mancala.getState().remove(user);
		else
			return new MancalaUser[0];

		if (mancala.getState().size() == 0)
			roomRepository.asMap().remove(room);

		return mancala.getState().keySet().toArray(new MancalaUser[0]);
	}

	public synchronized void removeRoom(Room room) {
		roomRepository.asMap().remove(room);
	}

	@Override
	public synchronized void init(Room room) {
		Mancala mancala = roomRepository.asMap().get(room);

		Set<Entry<MancalaUser, State>> entrySet = mancala.getState().entrySet();
		for (Entry<MancalaUser, State> entry : entrySet) {
			entry.setValue(new State());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	void reinit(CacheBuilderConfiguration<Integer, CacheBuilder> configuration, int thresholdArg) {
		this.threshold = thresholdArg;
		roomRepository = configuration.apply(threshold).build();
	}
}
