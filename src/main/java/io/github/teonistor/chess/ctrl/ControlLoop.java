package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.core.GameFactory;
import io.github.teonistor.chess.inter.DefinitelyInput;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.save.SaveLoad;
import io.vavr.Tuple2;

import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ControlLoop {

    private final SaveLoad saveLoad;
    private final GameFactory gameFactory;
    private final DefinitelyInput input;
    private final ScheduledExecutorService executorService;

    // Mutable state!
    private Game game;

    public ControlLoop(final SaveLoad saveLoad, final GameFactory gameFactory, final DefinitelyInput input) {
        this.saveLoad = saveLoad;
        this.gameFactory = gameFactory;
        this.input = input;
        executorService = newScheduledThreadPool(2);
    }

    public void launch() {
//        final InputEngine engine = input.create(this::processInput);
        // TODO Loop in executor, protect
//        executorService.submit(engine);

        final InputAction action = input.simpleInput();

        if (action.gameStateProvider().isPresent()) {
            System.err.println("[DEBUG] Game state provider provided - launching");
            game = gameFactory.create(action.gameStateProvider().get(), new InputProxy());
            // TODO protect
            executorService.scheduleWithFixedDelay(game, 1, 1, MILLISECONDS);

        } else if (!action.isExit()) {
            // Loop (by recursion) until we get something relevant
            launch();
        }
    }

    private class InputProxy implements Input {

        @Override
        public Tuple2<Position, Position> simpleInput() {
            final InputAction action = input.simpleInput();

            if (action.gameInput().isPresent()) {
                return action.gameInput().get();
            }

            if (action.isExit()) {
                System.err.println("[DEBUG] Exit action received - ending main control loop");
                executorService.shutdown();
                return null;

                // TODO This would be nicer, but it doesn't work due to the Game internally retrying to take input in case of invalid pair.
                // This is a legacy of the local variable state and ought to be fixed
//                return new Tuple2<>(Position.OutOfBoard, Position.OutOfBoard);
            }

            if (action.savePath().isPresent()) {
                final String path = action.savePath().get();
                System.err.println("[DEBUG] Saving game to " + path);
                saveLoad.saveState(game.getState(), path);
            }

            // Loop (by recursion) until we get something relevant
            return simpleInput();
        }

        @Override
        public String specialInput(final String... options) {
            return input.specialInput(options);
        }
    }
}
