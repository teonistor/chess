package io.github.teonistor.chess.inter;

import io.github.teonistor.chess.board.Position;
import io.vavr.Tuple2;

public interface Input {
    Tuple2<Position, Position> simpleInput();
    String specialInput(String... options);
}
