package io.github.teonistor.chess.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.factory.Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class ChessConfig {

    @Bean Factory factory() {
        return new Factory();
    }

    @Autowired
    public void configureObjectMapper(final ObjectMapper objectMapper) {
        factory().configureObjectMapper(objectMapper);
    }

    @Bean
    // Lazy to circumvent circular dependency
    public ControlLoop controlLoop(final @Lazy ChessCtrl chessCtrl) {
        return factory().createControlLoop(chessCtrl);
    }
}
