package io.github.teonistor.chess.ctrl;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.InputStream;

import static org.mockito.Mockito.verify;

@MockitoSettings
class LoadGameInputTest {

    @Test
    void execute(final @Mock ControlLoop loop, final @Mock InputStream stream) {
        new LoadGameInput(stream).execute(loop);
        verify(loop).loadGame(stream);
    }
}