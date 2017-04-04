package org.bol.game.portal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;

/**
 * Basic configuration of websockets, it enables a broker to listen particular topic.
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
 *
 */
public abstract class AbstractWebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	@Value("${broker.topic:/topic}")
	private String topic;

	
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(topic);
    }

}