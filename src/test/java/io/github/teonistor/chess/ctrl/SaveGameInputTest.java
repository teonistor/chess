package io.github.teonistor.chess.ctrl;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.OutputStream;

import static org.mockito.Mockito.verify;

@MockitoSettings
class SaveGameInputTest {

    @Test
    void execute(final @Mock ControlLoop loop, final @Mock OutputStream stream) {
        new SaveGameInput(stream).execute(loop);
        verify(loop).saveGame(stream);
    }
}