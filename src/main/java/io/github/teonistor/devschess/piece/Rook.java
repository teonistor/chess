package io.github.teonistor.devschess.piece;

import io.github.teonistor.devschess.Player;
import io.vavr.collection.List;

public class Rook extends LineMovingPiece {
    public Rook(Player player) {
        super(player, List.of(
                position -> position.up(),
                position -> position.left(),
                position -> position.right(),
                position -> position.down()));
    }
}
