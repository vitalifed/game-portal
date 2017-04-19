package org.steelrat.game.portal.mancala.intg;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

public abstract class AbstractSessionHandler extends StompSessionHandlerAdapter {

	private final AtomicReference<Throwable> failure;

	public AbstractSessionHandler(AtomicReference<Throwable> failure) {
		this.failure = failure;
	}

	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		this.getFailure().set(new Exception(headers.toString()));
	}

	@Override
	public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
		this.getFailure().set(ex);
	}

	@Override
	public void handleTransportError(StompSession session, Throwable ex) {
		this.getFailure().set(ex);
	}
	
	protected AtomicReference<Throwable> getFailure() {
		return failure;
	}
}
