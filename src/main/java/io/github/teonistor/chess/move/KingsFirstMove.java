package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.King;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.Map;


/**
 * A move which can land on a position if it's free or occupied by the opponent and creates an ordinary king at the destination instead of the picked up piece
 */
public class KingsFirstMove extends CaptureIndependentMove {

    public KingsFirstMove(final Position from, final Position to) {
        super(from, to);
    }

    @Override
    protected Map<Position,Piece> tinker(Map<Position, Piece> board) {
        return board.put(getTo(), new King(board.get(getTo()).get().getPlayer()));
    }
}
