package org.bol.game.portal.mancala.config;

import org.bol.game.portal.config.AbstractWebSocketConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class MancalaWebSocketConfig extends AbstractWebSocketConfig {

	@Value("${context.root:/mancala}")
	private String contextRoot;
	
	@Value("${end.point:/game-mancala-websocket}")
	private String endPoint;
	
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        super.configureMessageBroker(config);
        config.setApplicationDestinationPrefixes(contextRoot);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(endPoint).withSockJS();
    }

    
}