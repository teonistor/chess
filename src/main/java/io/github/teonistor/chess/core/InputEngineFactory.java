package io.github.teonistor.chess.core;

import io.github.teonistor.chess.ctrl.InputAction;
import io.github.teonistor.chess.inter.InputEngine;

import java.util.function.Consumer;

// Isolates the parameters which the control loop is responsible for from those which the factory is responsible for
public interface InputEngineFactory {
    InputEngine create(Consumer<InputAction> inputActionConsumer);
}
