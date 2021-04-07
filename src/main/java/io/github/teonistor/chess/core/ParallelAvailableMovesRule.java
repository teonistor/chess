package io.github.teonistor.chess.core;

import io.vavr.collection.Map;
import io.vavr.collection.Stream;

import static java.util.function.Function.identity;


public class ParallelAvailableMovesRule extends AvailableMovesRule {

    public ParallelAvailableMovesRule(final CheckRule rule) {
        super(rule);
    }

    @Override
    public Map<GameStateKey, GameState> computeAvailableMoves(final GameState state) {
        return Stream.of(Player.values())
             . map(state::withPlayer)
             . toMap(identity(), ignore -> GameStateKey.NIL)
             . flatMap((st, key) -> computeAvailableMoves(key, st))
             . flatMap((key, st) -> computeAvailableMoves(key, st));
    }

}
