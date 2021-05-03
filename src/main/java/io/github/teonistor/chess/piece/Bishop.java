package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.core.Player;
import io.vavr.collection.List;

public class Bishop extends LineMovingPiece {
    public Bishop(final Player player) {
        super(player, List.of(
                position -> position.up().left(),
                position -> position.up().right(),
                position -> position.down().left(),
                position -> position.down().right()));
    }
}
