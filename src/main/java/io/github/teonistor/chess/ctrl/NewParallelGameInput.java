package io.github.teonistor.chess.ctrl;

public class NewParallelGameInput implements Input {
    @Override
    public void execute(final ControlLoop loop) {
        loop.newParallelGame();
    }
}
