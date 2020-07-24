package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.King;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.Map;

import java.util.function.BiFunction;
import java.util.function.Function;


/**
 * A move which can land on a position if it's free or occupied by the opponent and creates an ordinary king at the destination instead of the picked up piece
 */
public class KingsFirstMove extends CaptureIndependentMove {

    public KingsFirstMove(final Position from, final Position to) {
        super(from, to);
    }

    @Override
    public <T> T execute(Map<Position,Piece> board, Function<Map<Position,Piece>, T> nonCapturingCallback, BiFunction<Map<Position,Piece>, Piece, T> capturingCallback) {
        return super.execute(board,
               newBoard -> nonCapturingCallback.apply(replaceTargetWithKing(newBoard)),
              (newBoard, capturedPiece) -> capturingCallback.apply(replaceTargetWithKing(newBoard), capturedPiece));
    }

    private Map<Position,Piece> replaceTargetWithKing(Map<Position, Piece> board) {
        return board.put(getTo(), new King(board.get(getTo()).get().getPlayer()));
    }
}
