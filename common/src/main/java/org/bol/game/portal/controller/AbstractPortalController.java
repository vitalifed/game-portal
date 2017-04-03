package org.bol.game.portal.controller;

import org.bol.game.portal.dto.User;
import org.bol.game.portal.flow.Workflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public abstract class AbstractPortalController<ActualUser extends User> {

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Value("${context.root}")
	private String contextRoot;

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

	public String getContextRoot() {
		return contextRoot;
	}

	protected abstract Workflow<ActualUser> getWorkflow();

}
