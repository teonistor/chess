package io.github.teonistor.chess.inter;

import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.Map;
import io.vavr.collection.Set;

public interface View {
    void refresh(Map<Position,Piece> board, Player player, Set<Piece> capturedPieces, Position source, Set<Position> targets);
    void announce(String message);
}
