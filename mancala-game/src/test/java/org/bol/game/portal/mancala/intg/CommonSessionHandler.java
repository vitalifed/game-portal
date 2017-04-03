package org.bol.game.portal.mancala.intg;

import java.lang.reflect.Type;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

public class CommonSessionHandler extends AbstractSessionHandler {

	private StompFrameHandler stompHandlerReference = new StompFrameHandler() {

		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
			getActualHandler().handleFrame(headers, payload);

		}

		@Override
		public Type getPayloadType(StompHeaders headers) {
			return getActualHandler().getPayloadType(headers);
		}
	};

	private StompFrameHandler actualHandler;

	private String token;

	private String roomName;

	private CountDownLatch sessionStarted;

	public CommonSessionHandler(AtomicReference<Throwable> failure, String token, String roomName,
			StompFrameHandler handler) {
		super(failure);
		this.actualHandler = handler;

		this.token = token;
		this.roomName = roomName;
	}

	public CommonSessionHandler(CountDownLatch sessionStarted, AtomicReference<Throwable> failure, String token,
			String roomName, StompFrameHandler handler) {
		this(failure, token, roomName, handler);

		this.sessionStarted = sessionStarted;

	}

	@Override
	public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
		session.subscribe("/topic/mancala/" + token + "/" + roomName + "/cmd", stompHandlerReference);
		if (sessionStarted != null)
			sessionStarted.countDown();
	}

	public void setActualHandler(StompFrameHandler actualHandler) {
		this.actualHandler = actualHandler;
	}

	public StompFrameHandler getActualHandler() {
		return actualHandler;
	}

}
