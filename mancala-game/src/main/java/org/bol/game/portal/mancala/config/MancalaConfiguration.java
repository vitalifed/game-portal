package org.bol.game.portal.mancala.config;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.bol.game.portal.dto.Room;
import org.bol.game.portal.mancala.dto.Mancala;
import org.bol.game.portal.mancala.flow.GameMancalaWorkflow;
import org.bol.game.portal.mancala.repository.CacheBuilderConfiguration;
import org.bol.game.portal.mancala.repository.MancalaRoomRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

@Configuration
public class MancalaConfiguration {

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
				System.out.println("removed");
				workflow.stopGame(notification.getKey().getName(), notification.getValue());
			}
		};
	}

}
