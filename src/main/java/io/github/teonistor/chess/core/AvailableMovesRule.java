package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Tuple2;
import io.vavr.collection.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AvailableMovesRule {

    private final CheckRule rule;

    public Map<GameStateKey, GameState> computeAvailableMoves(final GameState state) {
        final Map<Position, Piece> board = state.getBoard();
        final Player player = state.getPlayer();

        return board.filterValues(piece -> piece.getPlayer() == player)
              .flatMap((from, piece) -> () -> piece.computePossibleMoves(from)
              .filter(move -> move.validate(state))
              .map(move -> new Tuple2<>(GameStateKey.NIL.withInput(player, from, move.getTo()), move.execute(state)))
              .filter(targetAndState -> !rule.check(targetAndState._2.getBoard(), player))
              .iterator());
    }
}
