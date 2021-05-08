package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.core.Game;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.mockito.Mockito.verify;

@MockitoSettings
class GameInputTest {

    @Test
    void executeOnLoop(final @Mock ControlLoop loop) {
        final GameInput input = new GameInput() {
            public Game execute(final Game game) {
                throw new UnsupportedOperationException("Test stub");
            }
        };

        input.execute(loop);

        verify(loop).gameInput(input);
    }
}