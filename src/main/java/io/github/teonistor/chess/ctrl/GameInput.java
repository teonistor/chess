package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.core.Game;

public abstract class GameInput implements Input {

    public void execute(final ControlLoop loop) {
        loop.gameInput(this);
    }

    public abstract Game execute(Game game);
}
