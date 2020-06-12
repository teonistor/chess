package io.github.teonistor.devschess.piece;

import io.github.teonistor.devschess.Player;
import io.vavr.collection.List;

public class Queen extends LineMovingPiece {
    public Queen(Player player) {
        super(player, List.of(
                position -> position.up(),
                position -> position.left(),
                position -> position.right(),
                position -> position.down(),
                position -> position.up().left(),
                position -> position.up().right(),
                position -> position.down().left(),
                position -> position.down().right()));
    }
}
