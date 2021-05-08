package io.github.teonistor.chess.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.factory.Factory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@ComponentScan
@Configuration
@EnableWebSocketMessageBroker
public class ChessConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/chess-ws");
        registry.setApplicationDestinationPrefixes("/chess-ws");
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint("/chess-subscribe").withSockJS();
    }

    @Bean Factory factory() {
        return new Factory();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return factory().getObjectMapper();
    }

    @Bean
    // Lazy to circumvent circular dependency
    public ControlLoop controlLoop(final @Lazy ChessCtrl chessCtrl) {
        return factory().createControlLoop(chessCtrl);
    }
}
