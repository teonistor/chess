package io.github.teonistor.chess.ctrl;

public class NewStandardGameInput implements Input {
    @Override
    public void execute(final ControlLoop loop) {
        loop.newStandardGame();
    }
}
