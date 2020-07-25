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
public class CaptureIndependentMove implements Move {

    private final @NonNull Position from;
    private final @NonNull Position to;

    @Override
    public boolean validate(GameState state) {
        return !state.getBoard().get(to)
                .map(Piece::getPlayer)
                .filter(state.getPlayer()::equals)
                .isDefined();
    }

    @Override
    public GameState execute(GameState state) {
        Map<Position, Piece> board = state.getBoard();
        Piece fromPiece = board.get(from).get();
        Option<Piece> toPiece = board.get(to);
        if (toPiece.isDefined()) {
            return state.advance(tinker(board.remove(from).put(to, fromPiece)), toPiece.get());
        } else {
            return state.advance(tinker(board.remove(from).put(to, fromPiece)));
        }
    }

    // Easing implementation of KingsFirstMove. To be likely removed in future
    protected Map<Position,Piece> tinker(Map<Position,Piece> board) {
        return board;
    }
}
