package io.github.teonistor.devschess.inter;

import io.github.teonistor.devschess.Player;
import io.github.teonistor.devschess.board.Position;
import io.github.teonistor.devschess.piece.Piece;
import io.vavr.collection.Map;
import io.vavr.collection.Set;

public interface View {
    void refresh(Map<Position,Piece> board, Player player, Set<Piece> capturedPieces, Position source, Set<Position> targets);
    void announce(String message);
}
