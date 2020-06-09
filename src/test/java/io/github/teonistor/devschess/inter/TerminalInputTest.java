package io.github.teonistor.devschess.inter;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.OutputStream;

import static org.mockito.Mockito.mock;

class TerminalInputTest {

    private final InputStream inputStream = mock(InputStream.class);
    private final OutputStream outputStream = mock(OutputStream.class);

    @Test
    void takeInput() {
        final TerminalInput input = new TerminalInput(inputStream, outputStream);
//        when(inputStream) todo
    }
}