package org.bol.game.portal.mancala.flow;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.bol.game.portal.CommandBuilder;
import org.bol.game.portal.dto.Message;
import org.bol.game.portal.dto.Room;
import org.bol.game.portal.dto.User;
import org.bol.game.portal.exception.GamePortalException;
import org.bol.game.portal.exception.RoomOverflowException;
import org.bol.game.portal.exception.RoomsThresholdExceedException;
import org.bol.game.portal.flow.AbstractWorkflow;
import org.bol.game.portal.mancala.builder.ListMessageCommandBuilder;
import org.bol.game.portal.mancala.builder.MessageCommandBuilder;
import org.bol.game.portal.mancala.dto.Mancala;
import org.bol.game.portal.mancala.dto.MancalaUser;
import org.bol.game.portal.mancala.launcher.SimpleLauncher;
import org.bol.game.portal.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public abstract class GenericMancalaWorkflow extends AbstractWorkflow<MancalaUser>
		implements MancalaWorkflow<MancalaUser> {
	
	public static final String TOKEN = "token";
	public static final String ROOM = "room";
	
	private static final Logger logger = LoggerFactory.getLogger(GenericMancalaWorkflow.class);

	@Value("${topic.path.cmd}")
	private String pathCmd;

	@Override
	public CommandBuilder<?> createRoom(String room, MancalaUser user) {
		if (logger.isDebugEnabled())
			logger.debug("Create Room=" + room + ", user=" + user);

		MessageCommandBuilder builder = new MessageCommandBuilder();

		Map<String, String> mapping = new HashMap<>();
		mapping.put(TOKEN, user.getToken());
		mapping.put(ROOM, room);

		String topic = StrSubstitutor.replace(getPathCmd(), mapping);

		try {
			int size = getRoomRepository().addUser(new Room(room), user);
			CommandBuilder<SimpleLauncher<Message>> commandBuilder = builder
					.message("Welcome to Mancala: " + user.getName()).command(RemoteCommand.SUBSCRIBE.command())
					.topic(topic);
			SimpleLauncher<Message> launcher = commandBuilder.build();

			if (size == 2) {
				ListMessageCommandBuilder listBuilder = (ListMessageCommandBuilder) startGame(room);
				if (listBuilder != null) {
					listBuilder.insertLauncher(launcher, 0);

					return listBuilder;
				}
			}

			return commandBuilder;

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
	public CommandBuilder<?> leaveRoom(String roomName, MancalaUser user) {
		if (logger.isDebugEnabled())
			logger.debug("Leave room, "+ user + " left the room.");

		Room room = new Room(roomName);
		User[] remainingUsers = getRoomRepository().removeUser(new Room(roomName), user);

		ListMessageCommandBuilder builder = new ListMessageCommandBuilder();

		Map<String, String> mapping = new HashMap<>();
		mapping.put(ROOM, roomName);
		for (int i = 0; i < remainingUsers.length; i++) {
			mapping.put(TOKEN, remainingUsers[i].getToken());

			SimpleLauncher<Message> launcher = new MessageCommandBuilder()
					.message("User '" + user.getName() + "' left the room. Game is over.", Message.Type.WARNING)
					.command(RemoteCommand.DISCONNECT.command()).topic(StrSubstitutor.replace(getPathCmd(), mapping))
					.build();

			builder.addLauncher(launcher);
		}

		getRoomRepository().removeRoom(room);

		return builder;
	}

	protected abstract RoomRepository<MancalaUser, Mancala> getRoomRepository();

	protected String getPathCmd() {
		return pathCmd;
	}

}
