package org.bol.game.portal.mancala.intg.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.bol.game.portal.dto.Command;
import org.bol.game.portal.mancala.flow.RemoteCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

public class CommandStompHandler implements StompFrameHandler {

	private static final Logger logger = LoggerFactory.getLogger(CommandStompHandler.class);
	
	private AtomicReference<Throwable> failure;
	private CountDownLatch latch;

	private Set<RemoteCommand> commands;

	public CommandStompHandler(AtomicReference<Throwable> failure, CountDownLatch latch, RemoteCommand... commands) {
		this.failure = failure;
		this.latch = latch;
		this.commands = new HashSet<>(Arrays.asList(commands));
	}

	@Override
	public Type getPayloadType(StompHeaders headers) {
		return Command.class;
	}

	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		try {

			Command cmd = (Command) payload;
			RemoteCommand remoteCmd = RemoteCommand.valueOf(cmd.getCommand().toUpperCase());

			logger.info(payload.toString());
			
			if (remoteCmd==RemoteCommand.MOVE)
				return;
			
			assertTrue("Expect one of " + commands+", but actual="+remoteCmd, commands.remove(remoteCmd));
			commands.remove(0);
			
		} catch (Throwable t) {
			failure.set(t);
		} finally {
			latch.countDown();
		}
	}
	
	public void addCommand(RemoteCommand cmd){
		commands.add(cmd);
	}
}
