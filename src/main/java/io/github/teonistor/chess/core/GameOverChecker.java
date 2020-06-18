package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.Map;

public class GameOverChecker {
    public boolean isOver(Map<Position,Piece> board, Player player) {
        return false;
    }
}
