package io.github.teonistor.chess.factory;

import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.core.GameState;

public interface GameFactory {
    Game createNewGame();
    Game createGame(GameState state);
}
