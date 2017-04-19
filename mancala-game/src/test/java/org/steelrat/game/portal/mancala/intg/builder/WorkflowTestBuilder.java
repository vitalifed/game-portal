package org.steelrat.game.portal.mancala.intg.builder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.steelrat.game.portal.mancala.flow.RemoteCommand;
import org.steelrat.game.portal.mancala.intg.CommonSessionHandler;
import org.steelrat.game.portal.mancala.intg.handler.CommandStompHandler;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class WorkflowTestBuilder {

	private String token;
	private String room;
	private RemoteCommand[] commands;
	private int port;
	
	private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
	
	private AtomicReference<Throwable> failure;
	private WebSocketStompClient stompClient;
	private Operation<StompSession, CountDownLatch, String, String, Boolean> operation;
	private Validation<AtomicReference<Throwable>, CountDownLatch, Boolean> validation;

	public WorkflowTestBuilder token(String token){
		this.token = token;
		return this;
	}

	public WorkflowTestBuilder room(String room){
		this.room = room;
		return this;
	}

	public WorkflowTestBuilder commands(RemoteCommand... commands){
		this.commands = commands;
		return this;
	}
	
	public WorkflowTestBuilder client(WebSocketStompClient stompClient){
		this.stompClient = stompClient;
		return this;
	}

	public WorkflowTestBuilder port(int port){
		this.port = port;
		return this;
	}
	
	public WorkflowTestBuilder failure(AtomicReference<Throwable> failure){
		this.failure = failure;
		return this;
	}

	public WorkflowTestBuilder operation(Operation<StompSession, CountDownLatch, String, String, Boolean> operation){
		this.operation = operation;
		return this;
	}
	
	public WorkflowTestBuilder validation(Validation< AtomicReference<Throwable>, CountDownLatch, Boolean> validation){
		this.validation = validation;
		return this;
	}

	
	public WorkflowLauncher build(int countLatch) throws InterruptedException, ExecutionException{
		CountDownLatch latch = new CountDownLatch(countLatch);
		CountDownLatch sessionStarted = new CountDownLatch(1);

		CommonSessionHandler handler = new CommonSessionHandler(sessionStarted, failure, token, room,
				new CommandStompHandler(failure, latch, commands));

		ListenableFuture<StompSession> listenableFuture1 = stompClient
				.connect("ws://localhost:{port}/game-mancala-websocket", this.headers, handler, this.port);
		sessionStarted.await();
		StompSession session = listenableFuture1.get();
		
		return new WorkflowLauncher(latch, handler, session, token, room, failure, operation, validation);
	}
}
