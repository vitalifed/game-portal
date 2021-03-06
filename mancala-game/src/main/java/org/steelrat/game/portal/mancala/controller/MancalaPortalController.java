package org.steelrat.game.portal.mancala.controller;

import org.steelrat.game.portal.controller.AbstractPortalController;
import org.steelrat.game.portal.flow.Workflow;
import org.steelrat.game.portal.mancala.dto.Mancala;
import org.steelrat.game.portal.mancala.dto.MancalaUser;
import org.steelrat.game.portal.mancala.flow.GameMancalaWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * Basic mancala controller, rendering the movement made by user
 * {@link MancalaUser}.
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
 *
 */
@Controller
public class MancalaPortalController extends AbstractPortalController<MancalaUser, Mancala> {

	private static final Logger logger = LoggerFactory.getLogger(MancalaPortalController.class);

	@Autowired
	@Qualifier("workflow")
	private GameMancalaWorkflow workflow;

	@MessageMapping("/room/{room}/step/{idx}")
	public void stepGame(@Payload MancalaUser user, @DestinationVariable("room") String room,
			@DestinationVariable("idx") String idx) {
		if (logger.isDebugEnabled())
			logger.debug("Room=" + room + ", User=" + user + ". idx=" + idx);
		workflow.state(room, user, Integer.valueOf(idx)).operation(getSimpMessagingTemplate()).build().launch();
	}

	@Override
	protected Workflow<MancalaUser, Mancala> getWorkflow() {
		return workflow;
	}
}
