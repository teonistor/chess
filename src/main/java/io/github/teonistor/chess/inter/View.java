package io.github.teonistor.chess.inter;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Tuple2;
import io.vavr.collection.Map;
import io.vavr.collection.Traversable;

public interface View {
    void refresh(Map<Position, Piece> board, Traversable<Piece> capturedPieces, Traversable<Tuple2<Position, Position>> possibleMovesBlack, Traversable<Tuple2<Position, Position>> possibleMovesWhite, boolean promotionRequiredBlack, boolean promotionRequiredWhite);
    void announce(String message);
}
