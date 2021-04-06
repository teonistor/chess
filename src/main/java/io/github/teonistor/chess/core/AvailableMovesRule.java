package io.github.teonistor.chess.core;

import io.vavr.collection.Map;

public interface AvailableMovesRule {
    Map<GameStateKey, GameState> computeAvailableMoves(GameState state);
}
