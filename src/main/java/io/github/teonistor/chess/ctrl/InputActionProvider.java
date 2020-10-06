package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameStateProvider;
import io.github.teonistor.chess.save.SaveLoad;
import io.vavr.Tuple2;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class InputActionProvider {

    private final GameStateProvider initialStateProvider;
    private final SaveLoad saveLoad;

    public InputAction newGame() {
        return new InputAction() {
            @Override
            public Optional<GameStateProvider> gameStateProvider() {
                return Optional.of(initialStateProvider);
            }
        };
    }

    public InputAction loadGame(final String fileName) {
        return new InputAction() {
            @Override
            public Optional<GameStateProvider> gameStateProvider() {
                return Optional.of(saveLoad.doLoadState(fileName));
            }
        };
    }

    public InputAction gameInput(final Position from, final Position to) {
        return new InputAction() {
            @Override
            public Optional<Tuple2<Position, Position>> gameInput() {
                return Optional.of(new Tuple2<>(from, to));
            }
        };
    }

    public InputAction saveGame(final String fileName) {
        return new InputAction() {
            @Override
            public Optional<String> savePath() {
                return Optional.of(fileName);
            }
        };
    }

    public InputAction exit() {
        return new InputAction() {
            @Override
            public boolean isExit() {
                return true;
            }
        };
    }
}
