package io.github.teonistor.chess.ctrl;

import lombok.AllArgsConstructor;

import java.io.OutputStream;

@AllArgsConstructor
public class SaveGameInput implements Input {
    private final OutputStream outputStream;

    @Override
    public void execute(final ControlLoop loop) {
        loop.saveGame(outputStream);
    }
}
