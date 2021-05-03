package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import static io.vavr.Predicates.not;


/**
 * A move which can land on a position only if it's occupied by the opponent
 */
@AllArgsConstructor
@Getter
public class CapturingMove extends SingleOutcomeMove {

    private final @NonNull Position from;
    private final @NonNull Position to;

    @Override
    public boolean validate(final GameState state) {
        return state.getBoard().get(to)
                .map(Piece::getPlayer)
                .filter(not(state.getPlayer()::equals))
                .isDefined();
    }

    @Override
    public GameState executeSingleOutcome(final GameState state) {
        final Map<Position, Piece> board = state.getBoard();
        final Piece fromPiece = board.get(from).get();
        final Piece toPiece = board.get(to).get();
        return state.advance(board.remove(from).put(to, fromPiece), toPiece);
    }
}
