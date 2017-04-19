package org.steelrat.game.portal.mancala.config;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.steelrat.game.portal.dto.Room;
import org.steelrat.game.portal.mancala.dto.Mancala;
import org.steelrat.game.portal.mancala.flow.GameMancalaWorkflow;
import org.steelrat.game.portal.mancala.repository.CacheBuilderConfiguration;
import org.steelrat.game.portal.mancala.repository.MancalaRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * Bootstrap configuration of mancala-game microservice
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
 *
 */
@Configuration
public class MancalaConfiguration {

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Bean
	public MancalaRoomRepository roomRepository() {
		return new MancalaRoomRepository(repositoryConfiguration(listener(workflow()), 10, TimeUnit.MINUTES));
	}

	@Bean
	public GameMancalaWorkflow workflow() {
		return new GameMancalaWorkflow();
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
