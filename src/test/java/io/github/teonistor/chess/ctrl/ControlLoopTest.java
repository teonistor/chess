package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.core.GameStateProvider;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.InputEngine;
import io.github.teonistor.chess.save.SaveLoad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

// TODO This shit is untestable. Who wrote it come up front and take 10.000 facepalms.
class ControlLoopTest {

    @Mock private SaveLoad saveLoad;
    @Mock private Game game;
    @Mock private InputEngine inputEngine;
    @Mock private InputAction action;
    @Mock private GameStateProvider providerIn;

    private GameStateProvider providerOut;
    private Input input;
    private Consumer<InputAction> inputActionConsumer;

    private ControlLoop loop;

    @BeforeEach
    void setUp() {
        initMocks(this);
        loop = new ControlLoop(saveLoad, (p, i) -> {
            this.providerOut = p;
            this.input = i;
            return game;
        }, c -> {
            this.inputActionConsumer = c;
            return inputEngine;
        });
    }

    @Test
    void launch() {
        loop.launch();
        verify(inputEngine).run();
    }

    @Test
    void gameNotStartedAndGameStateProviderGiven() {
        setField(loop, "game", null);
        when(action.gameStateProvider()).thenReturn(Optional.of(providerIn));

        loop.launch();
        inputActionConsumer.accept(action);

        assertThat(providerOut).isSameAs(providerIn);
        assertThat(input).isNotNull();
        verify(action, atLeastOnce()).gameStateProvider();
        verify(inputEngine).run();
        verify(game).play();
    }

    @Test
    void processInputWithGame() {
    }
}