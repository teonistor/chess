package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameStateProvider;
import io.github.teonistor.chess.core.World;
import io.vavr.Tuple2;
import java.util.Optional;

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


    static InputAction newGame() {
        return new InputAction() {
            @Override
            public Optional<GameStateProvider> gameStateProvider() {
                return Optional.of(World.initialStateProvider());
            }
        };
    }

    static InputAction loadGame(final String fileName) {
        return new InputAction() {
            @Override
            public Optional<GameStateProvider> gameStateProvider() {
                return Optional.of(World.saveLoad().doLoadState(fileName));
            }
        };
    }

    static InputAction gameInput(final Position from, final Position to) {
        return new InputAction() {
            @Override
            public Optional<Tuple2<Position, Position>> gameInput() {
                return Optional.of(new Tuple2<>(from, to));
            }
        };
    }

    static InputAction saveGame(final String fileName) {
        return new InputAction() {
            @Override
            public Optional<String> savePath() {
                return Optional.of(fileName);
            }
        };
    }

    static InputAction exit() {
        return new InputAction() {
            @Override
            public boolean isExit() {
                return true;
            }
        };
    }
}
