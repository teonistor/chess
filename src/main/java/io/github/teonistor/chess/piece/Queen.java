package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.core.Player;

public class Queen extends LineMovingPiece implements AllDirectionsAwarePieceMixin {
    public Queen(final Player player) {
        super(player, allDirections);
    }
}
