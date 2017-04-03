package org.bol.game.portal.mancala.intg.builder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.bol.game.portal.mancala.intg.CommonSessionHandler;
import org.springframework.messaging.simp.stomp.StompSession;

public class WorkflowLauncher {
	private CountDownLatch latch;
	private CommonSessionHandler handler;
	private StompSession session;
	private Operation<StompSession, CountDownLatch, String, String, Boolean> operation;
	private String token;
	private String roomName;
	private Validation<AtomicReference<Throwable>, CountDownLatch, Boolean> validation;
	private AtomicReference<Throwable> failure;

	public WorkflowLauncher(CountDownLatch latch, CommonSessionHandler handler, StompSession session, String token,
			String roomName, AtomicReference<Throwable> failure,
			Operation<StompSession, CountDownLatch, String, String, Boolean> operation,
			Validation<AtomicReference<Throwable>, CountDownLatch, Boolean> validation) {
		super();
		this.latch = latch;
		this.handler = handler;
		this.session = session;
		this.operation = operation;
		this.token = token;
		this.roomName = roomName;
		this.validation = validation;
		this.failure = failure;
	}

	public CountDownLatch getLatch() {
		return latch;
	}

	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

	public CommonSessionHandler getHandler() {
		return handler;
	}

	public void setHandler(CommonSessionHandler handler) {
		this.handler = handler;
	}

	public StompSession getSession() {
		return session;
	}

	public void setSession(StompSession session) {
		this.session = session;
	}

	public Operation<StompSession, CountDownLatch, String, String, Boolean> getOperation() {
		return operation;
	}

	public void setOperation(Operation<StompSession, CountDownLatch, String, String, Boolean> operation) {
		this.operation = operation;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public WorkflowLauncher launch() throws Exception {
		operation.apply(getSession(), getLatch(), getToken(), getRoomName());
		return this;
	}

	public Validation<AtomicReference<Throwable>, CountDownLatch, Boolean> getValidation() {
		return validation;
	}

	public void setValidation(Validation<AtomicReference<Throwable>, CountDownLatch, Boolean> validation) {
		this.validation = validation;
	}

	public AtomicReference<Throwable> getFailure() {
		return failure;
	}

	public void setFailure(AtomicReference<Throwable> failure) {
		this.failure = failure;
	}

	public WorkflowLauncher validate() throws Exception {
		validation.apply(getFailure(), getLatch());
		return this;
	}
}
