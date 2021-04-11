package io.github.teonistor.chess.factory;

import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.inter.View;

public interface ControlLoopFactory {
    ControlLoop createControlLoop(View... views);
}
