package org.steelrat.game.portal.mancala.flow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.steelrat.game.portal.LauncherBuilder;
import org.steelrat.game.portal.dto.Room;
import org.steelrat.game.portal.mancala.builder.ListMessageLauncherBuilder;
import org.steelrat.game.portal.mancala.builder.MessageLauncherBuilder;
import org.steelrat.game.portal.mancala.builder.StateLauncherBuilder;
import org.steelrat.game.portal.mancala.dto.Mancala;
import org.steelrat.game.portal.mancala.dto.MancalaUser;
import org.steelrat.game.portal.mancala.dto.State;
import org.steelrat.game.portal.mancala.flow.util.RefineState;
import org.steelrat.game.portal.mancala.repository.MancalaRoomRepository;
import org.steelrat.game.portal.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Conerstone of Mancala game portal, the class implements the most critical
 * method that evaluates a request from user and updates the state of game.
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
 *
 */
public class GameMancalaWorkflow extends GenericMancalaWorkflow implements MancalaWorkflow<MancalaUser, Mancala> {

	private static final Logger logger = LoggerFactory.getLogger(GameMancalaWorkflow.class);

	@Autowired
	@Qualifier("roomRepository")
	private MancalaRoomRepository roomRepository;

	@Override
	public LauncherBuilder<?> state(String room, MancalaUser user, Integer idx) {
		if (logger.isDebugEnabled())
			logger.debug("State, room={}, user={}, idx={}", room, user, idx);
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

		ListMessageLauncherBuilder builder = new ListMessageLauncherBuilder();

		Map<String, String> mapping = new HashMap<>();
		mapping.put(ROOM, room);
		mapping.put(TOKEN, user.getToken());

		builder.addLauncher(new StateLauncherBuilder().payload(Arrays.asList(states))
				.command(RemoteCommand.STATE.command()).topic(StrSubstitutor.replace(getPathCmd(), mapping)).build());

		if (isCurrentUserTurn && !isGameOver) {
			builder.addLauncher(new MessageLauncherBuilder().message("Your move").command(RemoteCommand.MOVE.command())
					.topic(StrSubstitutor.replace(getPathCmd(), mapping)).build());
			mancala.setCurrentUser(user);
		} else if (isGameOver) {
			builder.addLauncher(new MessageLauncherBuilder()
					.message(states[0].getScore() > states[1].getScore() ? "You win"
							: states[0].getScore() == states[1].getScore() ? "Draw" : "You lost")
					.command(RemoteCommand.STOP.command()).topic(StrSubstitutor.replace(getPathCmd(), mapping))
					.build());
		}

		mapping.put(TOKEN, entry.getKey().getToken());

		states = states.clone();
		ArrayUtils.reverse(states);

		builder.addLauncher(new StateLauncherBuilder().payload(Arrays.asList(states))
				.command(RemoteCommand.STATE.command()).topic(StrSubstitutor.replace(getPathCmd(), mapping)).build());

		if (!isCurrentUserTurn && !isGameOver) {
			builder.addLauncher(new MessageLauncherBuilder().message("Your move").command(RemoteCommand.MOVE.command())
					.topic(StrSubstitutor.replace(getPathCmd(), mapping)).build());
			mancala.setCurrentUser(user);
		} else if (isGameOver) {
			builder.addLauncher(new MessageLauncherBuilder()
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
