package io.github.teonistor.chess.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.ctrl.InputActionProvider;
import io.github.teonistor.chess.factory.Factory;
import io.vavr.jackson.datatype.VavrModule;
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
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new VavrModule());
        objectMapper.registerModule(factory().getPieceSerialiser());
        return objectMapper;
    }

    @Bean
    public InputActionProvider inputActionProvider() {
        return factory().getInputActionProvider();
    }

    @Bean
    // Lazy to circumvent circular dependency
    public ControlLoop controlLoop(final @Lazy ChessCtrl chessCtrl) {
        return factory().createControlLoop(chessCtrl);
    }
}
