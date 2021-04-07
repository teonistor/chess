package io.github.teonistor.chess.core;

import io.vavr.Tuple2;
import io.vavr.collection.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AvailableMovesRule {
    private final CheckRule rule;

    public abstract Map<GameStateKey, GameState> computeAvailableMoves(GameState state);

    protected Map<GameStateKey, GameState> computeAvailableMoves(final GameStateKey key, final GameState state) {
        final Player player = state.getPlayer();

        return state.getBoard()
             . filterValues(piece -> piece.getPlayer() == player)
            // TODO Iterator workaround here due to Piece::computePossibleMoves returning the wrong (Java) Stream
             . flatMap((from, piece) -> () -> piece.computePossibleMoves(from)
             . filter(move -> move.validate(state))
             . map(move -> new Tuple2<>(key.withInput(player, from, move.getTo()), move.execute(state)))
             . filter(targetAndState -> !rule.check(targetAndState._2.getBoard(), player))
             . iterator());
    }
}