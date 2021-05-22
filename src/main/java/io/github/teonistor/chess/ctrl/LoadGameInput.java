package io.github.teonistor.chess.ctrl;

import lombok.AllArgsConstructor;

import java.io.InputStream;

@AllArgsConstructor
public class LoadGameInput implements Input {
    private final InputStream inputStream;

    @Override
    public void execute(final ControlLoop loop) {
        loop.loadGame(inputStream);
    }
}
