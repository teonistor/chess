package io.github.teonistor.chess.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.jackson.datatype.VavrModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
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

    @Autowired
    void configureObjectMapper(final ObjectMapper objectMapper) {
        objectMapper.registerModule(new VavrModule());
//        objectMapper.registerModule(pieceSerialiser.getPieceModule());
    }
}
