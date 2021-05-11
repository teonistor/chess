package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AvailableMovesRule {
    private final CheckRule rule;

    public abstract Map<GameStateKey, GameState> computeAvailableMoves(GameState state);

    protected Map<GameStateKey, GameState> computeAvailableMoves(final GameStateKey key, final GameState state) {
        final Player player = state.getPlayer();

            // Out of the whole board,
        return state.getBoard()

            // look only at the current player's pieces.
             . filterValues(piece -> piece.getPlayer() == player)

            // Out of all the moves each piece can make
             . flatMap((from, piece) -> piece.computePossibleMoves(from)

            // only care about the valid ones (e.g. not obstructed).
             . filter(move -> move.validate(state))

            // Take the results of all these moves
             . flatMap(move -> move.execute(state)

            // and key them by the input the player would need to provide in order to perform them.
             . mapKeys(op -> op.apply(key.withInput(player, from, move.getTo()))))

            // Finally, exclude moves which are still invalid due to the "bigger picture" (e.g. moving into check)
             . filter(targetAndState -> validateBoardwideRules(player, targetAndState._2.getBoard())));
    }

    protected boolean validateBoardwideRules(final Player player, final Map<Position, Piece> board) {
        return !rule.check(board, player);
    }
}