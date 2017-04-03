package org.bol.game.portal.mancala.repository;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bol.game.portal.dto.Room;
import org.bol.game.portal.exception.GamePortalException;
import org.bol.game.portal.exception.RoomOverflowException;
import org.bol.game.portal.exception.RoomsThresholdExceedException;
import org.bol.game.portal.mancala.dto.Mancala;
import org.bol.game.portal.mancala.dto.MancalaUser;
import org.bol.game.portal.mancala.dto.State;
import org.bol.game.portal.repository.RoomRepository;


public class MancalaRoomRepository implements RoomRepository<MancalaUser, Mancala> {

	private static final int ROOMS_AMOUNT_THRESHOLD = 100;
	
	private static final Map<Room, Mancala> roomRepository = new ConcurrentHashMap<>();

	public Mancala get(Room room) {
		return roomRepository.get(room);
	}

	public synchronized int addUser(Room room, MancalaUser user) throws GamePortalException  {
		Mancala mancala = roomRepository.get(room);
		if (mancala == null){
			if (roomRepository.size()==ROOMS_AMOUNT_THRESHOLD){
				throw new RoomsThresholdExceedException("The amount of the rooms exceeds threshold");
			}
			mancala = new Mancala();
		}

		if (mancala.getState().size() > 1)
			throw new RoomOverflowException("Room has already 2 players");

		mancala.getState().put(user, null);
		
		roomRepository.put(room, mancala);
		
		return mancala.getState().size();
	}
	
	public synchronized MancalaUser[] removeUser(Room room, MancalaUser user) {
		Mancala mancala = roomRepository.get(room);
		if (mancala!=null)
			mancala.getState().remove(user);
		else 
			return new MancalaUser[0];
		
		if (mancala.getState().size()==0)
			roomRepository.remove(room);
		
		return mancala.getState().keySet().toArray(new MancalaUser[0]);
	}

	public synchronized void removeRoom(Room room) {
		roomRepository.remove(room);
	}

	@Override
	public synchronized void startGame(Room room) {
		Mancala mancala = roomRepository.get(room);
		
		Set<Entry<MancalaUser, State>> entrySet = mancala.getState().entrySet();
		for (Entry<MancalaUser, State> entry : entrySet) {
			entry.setValue(new State());
		}
	}
	
	
}
