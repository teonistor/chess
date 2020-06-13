package io.github.teonistor.devschess.piece;

import io.github.teonistor.devschess.Player;
import io.github.teonistor.devschess.board.Position;
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
