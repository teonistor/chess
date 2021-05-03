package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.board.Position;
import io.vavr.collection.List;

import java.util.function.UnaryOperator;

public interface AllDirectionsAwarePieceMixin {
    List<UnaryOperator<Position>> allDirections = List.of(
            Position::up,
            Position::left,
            Position::right,
            Position::down,
            position -> position.up().left(),
            position -> position.up().right(),
            position -> position.down().left(),
            position -> position.down().right());
}
