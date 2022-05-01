package io.github.teonistor.chess.rule;

import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.GameStateKey;
import io.vavr.collection.Map;

public class StandardAvailableMovesRule extends AvailableMovesRule {

    public StandardAvailableMovesRule(final CheckRule rule) {
        super(rule);
    }

    @Override
    public Map<GameStateKey, GameState> computeAvailableMoves(final GameState state) {
        return computeAvailableMoves(GameStateKey.NIL, state);
    }
}
