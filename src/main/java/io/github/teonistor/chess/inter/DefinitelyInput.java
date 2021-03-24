package io.github.teonistor.chess.inter;

import io.github.teonistor.chess.ctrl.InputAction;

public interface DefinitelyInput {
    InputAction simpleInput();
    String specialInput(String... options);
}
