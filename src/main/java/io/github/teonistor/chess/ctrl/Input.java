package io.github.teonistor.chess.ctrl;

public interface Input {

    default void execute(final ControlLoop loop) {
        // Nothing
    }

}
