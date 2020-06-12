package io.github.teonistor.devschess.piece;

import io.github.teonistor.devschess.Player;
import io.vavr.collection.List;

public class Bishop extends LineMovingPiece {
    public Bishop(Player player) {
        super(player, List.of(
                position -> position.up().left(),
                position -> position.up().right(),
                position -> position.down().left(),
                position -> position.down().right()));
    }
}
