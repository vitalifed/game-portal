package org.steelrat.game.portal.mancala.repository;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.steelrat.game.portal.dto.Room;
import org.steelrat.game.portal.mancala.dto.Mancala;
import org.steelrat.game.portal.mancala.flow.GameMancalaWorkflow;
import org.steelrat.game.portal.mancala.flow.RemoteCommand;
import org.steelrat.game.portal.mancala.intg.AbstractIntgTest;
import org.steelrat.game.portal.mancala.intg.builder.WorkflowLauncher;
import org.steelrat.game.portal.mancala.intg.builder.WorkflowTestBuilder;
import org.steelrat.game.portal.mancala.intg.handler.CommandStompHandler;
import org.steelrat.game.portal.mancala.repository.CacheBuilderConfiguration;
import org.steelrat.game.portal.mancala.repository.MancalaRoomRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public class CacheEvictionTest extends AbstractIntgTest {

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@Autowired
	private GameMancalaWorkflow workflow;

	@Autowired
	@Qualifier("roomRepository")
	private MancalaRoomRepository roomRepository;

	private String roomName1 = "roomName1";
	private String token1 = "token1";
	private String token2 = "token2";

	final AtomicReference<Throwable> failure = new AtomicReference<>();

	@Before
	public void setUp(){
		roomRepository.reinit(repositoryConfiguration(listener(workflow), 10, TimeUnit.SECONDS), 10);
	}
	
	@Test
	public void subscribeAndPlay() throws Exception {

		///////////////////////////////////
		// Create a room and subscribe
		///////////////////////////////////
		WorkflowLauncher launcher = new WorkflowTestBuilder().token(token1).room(roomName1).client(getStompClient())
				.commands(RemoteCommand.SUBSCRIBE).failure(getFailure()).port(getPort()).operation(operation()).validation(check())
				.build(1).launch().validate();

		assertEquals(1, roomRepository.get(new Room(roomName1)).getState().size());

		///////////////////////////////////
		// Create a room, subscribe, start and move
		///////////////////////////////////

		WorkflowLauncher launcher2 = new WorkflowTestBuilder().token(token2).room(roomName1).client(getStompClient())
				.commands(RemoteCommand.SUBSCRIBE, RemoteCommand.START).failure(getFailure()).port(getPort()).operation(operation())
				.validation(check()).build(4);

		launcher.getHandler()
				.setActualHandler(new CommandStompHandler(getFailure(), launcher2.getLatch(), RemoteCommand.START));

		launcher2.launch().validate();
		assertEquals(2, roomRepository.get(new Room(roomName1)).getState().size());

		///////////////////////////////////
		// Wait cache eviction
		///////////////////////////////////
		
		CountDownLatch latch = new CountDownLatch(2);

		launcher.getHandler().setActualHandler(new CommandStompHandler(getFailure(), latch, RemoteCommand.DISCONNECT));
		launcher2.getHandler().setActualHandler(new CommandStompHandler(getFailure(), latch, RemoteCommand.DISCONNECT));

		launcher.setLatch(latch);
		launcher2.setLatch(latch);
		
		Thread.sleep(10000);
		
		launcher.validate();
		launcher2.validate();
	
	}

	@SuppressWarnings("rawtypes")
	private CacheBuilderConfiguration<Integer, CacheBuilder> repositoryConfiguration(
			RemovalListener<Room, Mancala> listener, int time, TimeUnit timeunit) {
		return (threshold) -> {
			Function<CacheBuilder<Object, Object>, CacheBuilder<Object, Object>> consumer = builder -> {
				if (listener != null)
					builder.removalListener(listener);

				return builder;
			};
			return consumer.apply(CacheBuilder.newBuilder().concurrencyLevel(4).maximumSize(threshold)
					.expireAfterAccess(time, timeunit));
		};
	}

	private RemovalListener<Room, Mancala> listener(GameMancalaWorkflow workflow) {
		return new RemovalListener<Room, Mancala>() {
			@Override
			public void onRemoval(RemovalNotification<Room, Mancala> notification) {
				workflow.stopGame(notification.getKey().getName(), notification.getValue())
						.operation(simpMessagingTemplate).build().launch();
			}
		};
	}

}
