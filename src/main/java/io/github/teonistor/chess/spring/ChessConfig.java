package io.github.teonistor.chess.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.teonistor.chess.board.InitialBoardProvider;
import io.github.teonistor.chess.core.CheckRule;
import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.core.GameOverChecker;
import io.github.teonistor.chess.core.InitialStateProvider;
import io.github.teonistor.chess.core.PieceSerialiser;
import io.github.teonistor.chess.core.UnderAttackRule;
import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.ctrl.InputActionProvider;
import io.github.teonistor.chess.piece.PieceBox;
import io.github.teonistor.chess.save.SaveLoad;
import io.vavr.collection.Stream;
import io.vavr.jackson.datatype.VavrModule;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@ComponentScan
@Configuration
@EnableWebSocketMessageBroker
public class ChessConfig implements WebSocketMessageBrokerConfigurer, BeanDefinitionRegistryPostProcessor {

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/chess-ws");
        registry.setApplicationDestinationPrefixes("/chess-ws");
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint("/chess-subscribe").withSockJS();
    }

    @Bean
    public String configureObjectMapper(final ObjectMapper objectMapper, final PieceSerialiser pieceSerialiser) {
        objectMapper.registerModule(new VavrModule());
        objectMapper.registerModule(pieceSerialiser.getPieceModule());
        return "A";
    }

    // How compact is that!
    @Override
    public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) throws BeansException {
        Stream.of(UnderAttackRule.class, CheckRule.class, GameOverChecker.class, PieceBox.class, InitialBoardProvider.class, InitialStateProvider.class, PieceSerialiser.class, SaveLoad.class, InputActionProvider.class)
            .zip(Stream.of("underAttackRule", "checkRule", "gameOverChecker", "pieceBox", "initialBoardProvider", "initialStateProvider", "pieceSerialiser", "saveLoad", "inputActionProvider"))
            .forEach(classAndName -> {
                final GenericBeanDefinition definition = new GenericBeanDefinition();
                definition.setBeanClass(classAndName._1);
                registry.registerBeanDefinition(classAndName._2, definition);
            });
    }

    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory ignore) throws BeansException {
        // Nothing
    }

    @Bean
    public String controlLoopFactory(SaveLoad saveLoad, CheckRule checkRule, GameOverChecker gameOverChecker, ChessCtrl chessCtrl) {
        new ControlLoop(saveLoad,
                (p, i) -> new Game(p, checkRule, gameOverChecker, i, chessCtrl),
                chessCtrl)
            .launch();
        return "B";
    }
}
