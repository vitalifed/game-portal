package org.bol.game.portal.controller;

import org.bol.game.portal.dto.Game;
import org.bol.game.portal.dto.User;
import org.bol.game.portal.flow.Workflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Convenient superclass for controller actual implementations, it supplies
 * already typical operations can take place in the framework of game portal.
 *
 * @author VF85400
 *
 * @param <ActualUser>
 *            Actual implementation of User
 * @param <ActualGame>
 *            Actual implementation of Game
 */
public abstract class AbstractPortalController<ActualUser extends User, ActualGame extends Game<ActualUser>> {

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@MessageMapping("/room/{room}/create")
	public void create(@Payload ActualUser user, @DestinationVariable("room") String room) {
		getWorkflow().createRoom(room, user).operation(getSimpMessagingTemplate()).build().launch();
	}

	@MessageMapping("/room/{room}/leave")
	public void leave(@Payload ActualUser user, @DestinationVariable("room") String room) {
		getWorkflow().leaveRoom(room, user).operation(simpMessagingTemplate).build().launch();
	}

	protected SimpMessagingTemplate getSimpMessagingTemplate() {
		return simpMessagingTemplate;
	}

	protected abstract Workflow<ActualUser, ActualGame> getWorkflow();

}
