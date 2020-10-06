package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameStateProvider;
import io.vavr.Tuple2;
import java.util.Optional;

// Defaults tested by InputActionProviderTest
public interface InputAction {

    default Optional<GameStateProvider> gameStateProvider() {
        return Optional.empty();
    }

    default Optional<Tuple2<Position,Position>> gameInput() {
        return Optional.empty();
    }

    default Optional<String> savePath() {
        return Optional.empty();
    }

    default boolean isExit() {
        return false;
    }
}
