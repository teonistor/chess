package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.core.GameFactory;
import io.github.teonistor.chess.core.InputEngineFactory;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.InputEngine;
import io.github.teonistor.chess.save.SaveLoad;
import io.vavr.Tuple2;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.apache.commons.lang3.exception.ExceptionUtils.rethrow;

public class ControlLoop {

    private final SaveLoad saveLoad;
    private final GameFactory gameFactory;
    // FYI shutdownNow() - "typical implementations will cancel via Thread.interrupt()" so I can utilise this carefully but usefully
    private final InputEngineFactory inputEngineFactory;
    private final BlockingQueue<Tuple2<Position, Position>> gameInputActions;
    private final ExecutorService executorService;

    // Mutable state!
    private Game game;

    public ControlLoop(SaveLoad saveLoad, GameFactory gameFactory, InputEngineFactory inputEngineFactory) {
        this.saveLoad = saveLoad;
        this.gameFactory = gameFactory;
        this.inputEngineFactory = inputEngineFactory;
        this.gameInputActions = new ArrayBlockingQueue<>(1);
        executorService = newFixedThreadPool(2);
    }

    public void launch() {
        final InputEngine engine = inputEngineFactory.create(this::processInput);
        executorService.submit(engine);
    }

    private void processInput(InputAction action) {
        if (game == null)
            processInputWithoutGame(action);
        else
            processInputWithGame(action);
    }

    private void processInputWithoutGame(final InputAction action) {
        if (action.gameStateProvider().isPresent()) {
            System.err.println("[DEBUG] Game state provider provided - launching");
            game = gameFactory.create(action.gameStateProvider().get(), new InputProxy());
            executorService.submit(game::play);
        }

        if (action.isExit()) {
            System.err.println("[DEBUG] Exit action received - ending early control loop");
            executorService.shutdownNow();
        }
    }

    public void processInputWithGame(InputAction action) {
        if (action.gameInput().isPresent()) {
            try {
                gameInputActions.put(action.gameInput().get());
                // TODO Nicer?
            } catch (InterruptedException e) {
                rethrow(e);
            }
        }

        if (action.savePath().isPresent()) {
//            saveLoad.save(game.getState(), action.savePath().get());
//            System.err.println("[DEBUG] Game saved");
            System.err.println("[TODO] Cannot save game because I busted the interfaces!");
        }

        if (action.isExit()) {
            System.err.println("[DEBUG] Exit action received - ending main control loop");
            executorService.shutdownNow();
        }
    }

    //class ViewState {
//
//    private GameState gameState;
//    private boolean specialInputRequested;
//    private String[] specialInputChoices;
//
//
//}
//
    private class InputProxy implements Input {

        @Override
        public Tuple2<Position, Position> simpleInput() {
            try {
                return gameInputActions.take();
                // TODO Nicer?
            } catch (InterruptedException e) {
                return rethrow(e);
            }
        }

        @Override
        public String specialInput(String... options) {
            throw new UnsupportedOperationException("When we have this we'll drink champagne");
        }

    }
}
