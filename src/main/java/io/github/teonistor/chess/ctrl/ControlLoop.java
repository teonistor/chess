package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.core.GameCondition;
import io.github.teonistor.chess.core.GameFactory;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.GameStateProvider;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.InputEngine;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.save.SaveLoad;
import io.vavr.Tuple2;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import static java.awt.Color.white;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.apache.commons.lang3.exception.ExceptionUtils.rethrow;

public class ControlLoop {

    private final InputEngine inputEngine;
    private final View view;
    private final SaveLoad saveLoad;
    private final GameFactory gameFactory;
    private final BlockingQueue<Tuple2<Position,Position>> gameInputActions;
    // FYI shutdownNow() - "typical implementations will cancel via Thread.interrupt()" so I can utilise this carefully but usefully
    private final ExecutorService executorService;

    private Game game;

    public ControlLoop(final InputEngine inputEngine, final View view, SaveLoad saveLoad, GameFactory gameFactory) {
        this.inputEngine = inputEngine;
        this.view = view;
        this.saveLoad = saveLoad;
        this.gameFactory = gameFactory;
        this.gameInputActions = new ArrayBlockingQueue<>(1);
        executorService = newFixedThreadPool(1);
    }

    private Input createInputProxy() {
        return new Input() {
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
        };
    }
    
    public void processInput(InputAction action) {
        if (action.gameInput().isPresent()) {
            try {
                gameInputActions.put(action.gameInput().get());
            // TODO Nicer?
            } catch (InterruptedException e) {
                rethrow(e);
            }

        } else if (action.savePath().isPresent()) {
//            saveLoad.save(game.getState(), action.savePath().get());
//            System.err.println("[DEBUG] Game saved");
            System.err.println("[TODO] Cannot save game because I busted the interfaces!");
//            continue;
        }

        if (action.isExit()) {
            System.err.println("[DEBUG] Exit action received - ending main control loop");
            // TODO halt game
        }
    }

    private void processInputWithoutGame(final InputAction action) {
        if (action.gameStateProvider().isPresent()) {
            System.err.println("[DEBUG] Game state provider provided - launching");
            game = gameFactory.createGame(action.gameStateProvider().get(), createInputProxy(), view);
            executorService.submit(game::play);
        }

        if (action.isExit()) {
            System.err.println("[DEBUG] Exit action received - ending early control loop");
            // TODO We may have sweet FA to do in this case because the input engine should already know to stop
        }
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
//class InputSplitterProxy {
//
//    private final ArrayBlockingQueue<InputAction> gameInputAction;
//    private final ArrayBlockingQueue<InputAction> controlInputAction;
//    private final Input gameInput;
//    private final Input controlInput;
//
//    public InputSplitterProxy() {
//        gameInputAction = new ArrayBlockingQueue<>(1);
//        controlInputAction = new ArrayBlockingQueue<>(1);
//        gameInput = new Input() {
//
//        };
//        controlInput = new Input() {
//
//        };
//    }
//}

