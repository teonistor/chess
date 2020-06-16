package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.board.Position;
import io.vavr.collection.List;

public class Queen extends LineMovingPiece {
    public Queen(Player player) {
        super(player, List.of(
                Position::up,
                Position::left,
                Position::right,
                Position::down,
                position -> position.up().left(),
                position -> position.up().right(),
                position -> position.down().left(),
                position -> position.down().right()));
    }
}