package org.bol.game.portal.mancala.flow;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.bol.game.portal.LauncherBuilder;
import org.bol.game.portal.dto.Message;
import org.bol.game.portal.dto.Room;
import org.bol.game.portal.dto.User;
import org.bol.game.portal.exception.GamePortalException;
import org.bol.game.portal.exception.RoomOverflowException;
import org.bol.game.portal.exception.RoomsThresholdExceedException;
import org.bol.game.portal.flow.Workflow;
import org.bol.game.portal.mancala.builder.ListMessageLauncherBuilder;
import org.bol.game.portal.mancala.builder.MessageLauncherBuilder;
import org.bol.game.portal.mancala.dto.Mancala;
import org.bol.game.portal.mancala.dto.MancalaUser;
import org.bol.game.portal.mancala.launcher.SimpleLauncher;
import org.bol.game.portal.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * Abstract class that implements generic methods of {@link Workflow}
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
 *
 */
public abstract class GenericMancalaWorkflow implements Workflow<MancalaUser, Mancala> {

	public static final String TOKEN = "token";
	public static final String ROOM = "room";

	private static final Logger logger = LoggerFactory.getLogger(GenericMancalaWorkflow.class);

	@Value("${topic.path.cmd}")
	private String pathCmd;

	@Override
	public LauncherBuilder<?> createRoom(String room, MancalaUser user) {
		if (logger.isDebugEnabled())
			logger.debug("Create Room=" + room + ", user=" + user);

		MessageLauncherBuilder builder = new MessageLauncherBuilder();

		Map<String, String> mapping = new HashMap<>();
		mapping.put(TOKEN, user.getToken());
		mapping.put(ROOM, room);

		String topic = StrSubstitutor.replace(getPathCmd(), mapping);

		try {
			int size = getRoomRepository().addUser(new Room(room), user);
			LauncherBuilder<SimpleLauncher<Message>> launcherBuilder = builder
					.message("Welcome to Mancala: " + user.getName()).command(RemoteCommand.SUBSCRIBE.command())
					.topic(topic);
			SimpleLauncher<Message> launcher = launcherBuilder.build();

			if (size == 2) {
				ListMessageLauncherBuilder listBuilder = (ListMessageLauncherBuilder) startGame(room);
				if (listBuilder != null) {
					listBuilder.insertLauncher(launcher, 0);

					return listBuilder;
				}
			}

			return launcherBuilder;

		} catch (RoomOverflowException e) {
			logger.error(e.getMessage(), e);
			return builder.message("Unfortunatly a room '" + room + "' is full. Please specify another name.",
					Message.Type.WARNING).command(RemoteCommand.REFUSE.command()).topic(topic);
		} catch (RoomsThresholdExceedException e) {
			logger.error(e.getMessage(), e);
			return builder.message(
					"Unfortunatly a room '" + room + "' can't be created. The total amount exceeds the threshold.",
					Message.Type.WARNING).command(RemoteCommand.REFUSE.command()).topic(topic);
		} catch (GamePortalException e) {
			logger.error(e.getMessage(), e);
			return builder.message("Internal server error", Message.Type.WARNING)
					.command(RemoteCommand.DISCONNECT.command()).topic(topic);
		}
	}

	@Override
	public LauncherBuilder<?> leaveRoom(String roomName, MancalaUser user) {
		if (logger.isDebugEnabled())
			logger.debug("Leave room, " + user + " left the room.");

		Room room = new Room(roomName);
		User[] remainingUsers = getRoomRepository().removeUser(new Room(roomName), user);

		ListMessageLauncherBuilder builder = new ListMessageLauncherBuilder();

		Map<String, String> mapping = new HashMap<>();
		mapping.put(ROOM, roomName);
		for (int i = 0; i < remainingUsers.length; i++) {
			mapping.put(TOKEN, remainingUsers[i].getToken());

			SimpleLauncher<Message> launcher = new MessageLauncherBuilder()
					.message("User '" + user.getName() + "' left the room. Game is over.", Message.Type.WARNING)
					.command(RemoteCommand.DISCONNECT.command()).topic(StrSubstitutor.replace(getPathCmd(), mapping))
					.build();

			builder.addLauncher(launcher);
		}

		getRoomRepository().removeRoom(room);

		return builder;
	}

	@Override
	public LauncherBuilder<?> startGame(String roomName) {
		if (logger.isDebugEnabled())
			logger.debug("Start game in Room=" + roomName);

		Room room = new Room(roomName);

		Map<MancalaUser, ?> map = getRoomRepository().get(room).getState();
		if (map.size() == 2) {
			getRoomRepository().init(room);

			ListMessageLauncherBuilder builder = new ListMessageLauncherBuilder();

			MancalaUser[] mancalaUsers = map.keySet().toArray(new MancalaUser[2]);
			Map<String, String> mapping = new HashMap<>();
			mapping.put(ROOM, roomName);

			for (int i = 0; i < mancalaUsers.length; i++) {
				mapping.put(TOKEN, mancalaUsers[i].getToken());

				SimpleLauncher<Message> launcher = new MessageLauncherBuilder().message("The game is started")
						.command(RemoteCommand.START.command()).topic(StrSubstitutor.replace(getPathCmd(), mapping))
						.build();

				builder.addLauncher(launcher);

				if (i == 0) {
					builder.addLauncher(new MessageLauncherBuilder().message("Let's start, your move")
							.command(RemoteCommand.MOVE.command()).topic(StrSubstitutor.replace(getPathCmd(), mapping))
							.build());
					getRoomRepository().get(room).setCurrentUser(mancalaUsers[i]);
				}
			}

			return builder;
		}
		return null;
	}
	@Override
	public LauncherBuilder<?> stopGame(String roomName, Mancala game) {
		if (logger.isDebugEnabled())
			logger.debug("Stop game, room = " + roomName);
		ListMessageLauncherBuilder builder = new ListMessageLauncherBuilder();

		MancalaUser[] remainingUsers = game.getState().keySet().toArray(new MancalaUser[0]);
		
		Map<String, String> mapping = new HashMap<>();
		mapping.put(ROOM, roomName);
		for (int i = 0; i < remainingUsers.length; i++) {
			mapping.put(TOKEN, remainingUsers[i].getToken());

			SimpleLauncher<Message> launcher = new MessageLauncherBuilder()
					.message("Game is over", Message.Type.WARNING)
					.command(RemoteCommand.DISCONNECT.command()).topic(StrSubstitutor.replace(getPathCmd(), mapping))
					.build();

			builder.addLauncher(launcher);
		}
		return builder;
	}
	
	protected abstract RoomRepository<MancalaUser, Mancala> getRoomRepository();

	protected String getPathCmd() {
		return pathCmd;
	}

}
