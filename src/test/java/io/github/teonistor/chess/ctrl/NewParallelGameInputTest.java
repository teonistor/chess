package io.github.teonistor.chess.ctrl;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.mockito.Mockito.verify;

@MockitoSettings
class NewParallelGameInputTest {

    @Test
    void execute(final @Mock ControlLoop loop) {
        new NewParallelGameInput().execute(loop);
        verify(loop).newParallelGame();
    }
}