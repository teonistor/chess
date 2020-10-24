package io.github.teonistor.chess.inter;

import io.github.teonistor.chess.ctrl.InputAction;

import java.util.function.Consumer;

// TODO Rename
public interface InputEngine {

    Runnable prepare(Consumer<InputAction> inputActionConsumer);
    void stop();
}
