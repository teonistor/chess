package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;


/**
 * A move which can land on a position if it's free or occupied by the opponent
 */
@AllArgsConstructor
@Getter
public class CaptureIndependentMove extends SingleOutcomeMove {

    private final @NonNull Position from;
    private final @NonNull Position to;

    @Override
    public boolean validate(final GameState state) {
        return !state.getBoard().get(to)
                .map(Piece::getPlayer)
                .filter(state.getPlayer()::equals)
                .isDefined();
    }

    @Override
    protected GameState executeSingleOutcome(final GameState state, final Piece pieceToPlace) {
        final Map<Position, Piece> board = state.getBoard();
        final Option<Piece> pieceAtTarget = board.get(to);
        if (pieceAtTarget.isDefined()) {
            return state.advance(board.remove(from).put(to, pieceToPlace), pieceAtTarget.get());
        } else {
            return state.advance(board.remove(from).put(to, pieceToPlace));
        }
    }
}
