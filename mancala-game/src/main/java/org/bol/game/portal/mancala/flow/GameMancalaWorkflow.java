package org.bol.game.portal.mancala.flow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.bol.game.portal.CommandBuilder;
import org.bol.game.portal.dto.Message;
import org.bol.game.portal.dto.Room;
import org.bol.game.portal.mancala.builder.ListMessageCommandBuilder;
import org.bol.game.portal.mancala.builder.MessageCommandBuilder;
import org.bol.game.portal.mancala.builder.StateCommandBuilder;
import org.bol.game.portal.mancala.dto.Mancala;
import org.bol.game.portal.mancala.dto.MancalaUser;
import org.bol.game.portal.mancala.dto.State;
import org.bol.game.portal.mancala.flow.util.RefineState;
import org.bol.game.portal.mancala.launcher.SimpleLauncher;
import org.bol.game.portal.mancala.repository.MancalaRoomRepository;
import org.bol.game.portal.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class GameMancalaWorkflow extends GenericMancalaWorkflow {

	private static final Logger logger = LoggerFactory.getLogger(GameMancalaWorkflow.class);

	@Autowired
	@Qualifier("roomRepository")
	private MancalaRoomRepository roomRepository;

	@Override
	public CommandBuilder<?> startGame(String roomName) {
		if (logger.isDebugEnabled())
			logger.debug("Start game in Room=" + roomName);

		Room room = new Room(roomName);

		Map<MancalaUser, ?> map = roomRepository.get(room).getState();
		if (map.size() == 2) {
			roomRepository.init(room);

			ListMessageCommandBuilder builder = new ListMessageCommandBuilder();

			MancalaUser[] mancalaUsers = map.keySet().toArray(new MancalaUser[2]);
			Map<String, String> mapping = new HashMap<>();
			mapping.put(ROOM, roomName);

			for (int i = 0; i < mancalaUsers.length; i++) {
				mapping.put(TOKEN, mancalaUsers[i].getToken());

				SimpleLauncher<Message> launcher = new MessageCommandBuilder().message("The game is started")
						.command(RemoteCommand.START.command()).topic(StrSubstitutor.replace(getPathCmd(), mapping))
						.build();

				builder.addLauncher(launcher);

				if (i == 0) {
					builder.addLauncher(new MessageCommandBuilder().message("Let's start, your move")
							.command(RemoteCommand.MOVE.command()).topic(StrSubstitutor.replace(getPathCmd(), mapping))
							.build());
					roomRepository.get(room).setCurrentUser(mancalaUsers[i]);
				}
			}

			return builder;
		}
		return null;
	}

	@Override
	public CommandBuilder<?> state(String room, MancalaUser user, Integer idx) {
		Mancala mancala = getRoomRepository().get(new Room(room));

		Map<MancalaUser, State> tmp = new HashMap<>();
		tmp.putAll(mancala.getState());

		Map.Entry<MancalaUser, State> entry;

		State[] states = new State[] { tmp.remove(user), (entry = tmp.entrySet().iterator().next()).getValue() };
		boolean isCurrentUserTurn = RefineState.refine(states, idx);

		// check game is over
		boolean isGameOver = checkIfGameOver(states);
		if (isGameOver) {
			RefineState.finishGame(states);
		}

		ListMessageCommandBuilder builder = new ListMessageCommandBuilder();

		Map<String, String> mapping = new HashMap<>();
		mapping.put(ROOM, room);
		mapping.put(TOKEN, user.getToken());

		builder.addLauncher(new StateCommandBuilder().payload(Arrays.asList(states))
				.command(RemoteCommand.STATE.command()).topic(StrSubstitutor.replace(getPathCmd(), mapping)).build());

		if (isCurrentUserTurn && !isGameOver) {
			builder.addLauncher(new MessageCommandBuilder().message("Your move").command(RemoteCommand.MOVE.command())
					.topic(StrSubstitutor.replace(getPathCmd(), mapping)).build());
			mancala.setCurrentUser(user);
		} else if (isGameOver) {
			builder.addLauncher(new MessageCommandBuilder()
					.message(states[0].getScore() > states[1].getScore() ? "You win"
							: states[0].getScore() == states[1].getScore() ? "Draw" : "You lost")
					.command(RemoteCommand.STOP.command()).topic(StrSubstitutor.replace(getPathCmd(), mapping))
					.build());
		}

		mapping.put(TOKEN, entry.getKey().getToken());

		states = states.clone();
		ArrayUtils.reverse(states);

		builder.addLauncher(new StateCommandBuilder().payload(Arrays.asList(states))
				.command(RemoteCommand.STATE.command()).topic(StrSubstitutor.replace(getPathCmd(), mapping)).build());

		if (!isCurrentUserTurn && !isGameOver) {
			builder.addLauncher(new MessageCommandBuilder().message("Your move").command(RemoteCommand.MOVE.command())
					.topic(StrSubstitutor.replace(getPathCmd(), mapping)).build());
			mancala.setCurrentUser(user);
		} else if (isGameOver) {
			builder.addLauncher(new MessageCommandBuilder()
					.message(states[0].getScore() > states[1].getScore() ? "You win"
							: states[0].getScore() == states[1].getScore() ? "Draw" : "You lost")
					.command(RemoteCommand.STOP.command()).topic(StrSubstitutor.replace(getPathCmd(), mapping))
					.build());
		}

		return builder;
	}

	protected boolean checkIfGameOver(State[] states) {
		return isAllZero(states[0].getState()) || isAllZero(states[1].getState());
	}

	private boolean isAllZero(int[] state) {
		for (int i = 0; i < state.length; i++) {
			if (state[i] > 0)
				return false;
		}

		return true;
	}

	protected RoomRepository<MancalaUser, Mancala> getRoomRepository() {
		return roomRepository;
	}

}
