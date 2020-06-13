package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.board.Position;
import io.vavr.collection.List;

public class Rook extends LineMovingPiece {
    public Rook(Player player) {
        super(player, List.of(
                Position::up,
                Position::left,
                Position::right,
                Position::down));
    }
}
