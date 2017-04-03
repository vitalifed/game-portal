package org.bol.game.portal.mancala.config;

import org.bol.game.portal.mancala.flow.GameMancalaWorkflow;
import org.bol.game.portal.mancala.repository.MancalaRoomRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MancalaConfiguration {

	@Bean
	public MancalaRoomRepository roomRepository() {
		return new MancalaRoomRepository();
	}
	
	@Bean
	public GameMancalaWorkflow workflow() {
		return new GameMancalaWorkflow();
	}
	
}
