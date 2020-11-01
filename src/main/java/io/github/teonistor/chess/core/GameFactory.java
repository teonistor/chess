package io.github.teonistor.chess.core;

import io.github.teonistor.chess.inter.Input;

// Isolates the parameters which the control loop is responsible for from those which the factory is responsible for
public interface GameFactory {
    Game create(GameStateProvider provider, Input input);
}
