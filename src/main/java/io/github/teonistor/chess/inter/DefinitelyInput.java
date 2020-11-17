package io.github.teonistor.chess.inter;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.ctrl.InputAction;
import io.vavr.Tuple2;

public interface DefinitelyInput {
    InputAction simpleInput();
    String specialInput(String... options);
}
