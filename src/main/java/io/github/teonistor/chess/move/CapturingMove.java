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
public class CapturingMove implements Move {

    private final @NonNull Position from;
    private final @NonNull Position to;

    @Override
    public boolean validate(GameState state) {
        return state.getBoard().get(to)
                .map(Piece::getPlayer)
                .filter(not(state.getPlayer()::equals))
                .isDefined();
    }

    @Override
    public GameState execute(GameState state) {
        Map<Position, Piece> board = state.getBoard();
        Piece fromPiece = board.get(from).get();
        Piece toPiece = board.get(to).get();
        return state.advance(board.remove(from).put(to, fromPiece), toPiece);
    }
}
