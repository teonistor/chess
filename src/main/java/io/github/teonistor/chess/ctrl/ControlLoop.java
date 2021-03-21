package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.factory.GameFactory;
import io.github.teonistor.chess.save.SaveLoad;
import io.vavr.Tuple2;

public class ControlLoop {

    private final SaveLoad saveLoad;
    private final GameFactory gameFactory;

    // The only allowed mutable state
    private Game game;

    public ControlLoop(final SaveLoad saveLoad, final GameFactory gameFactory) {
        this.saveLoad = saveLoad;
        this.gameFactory = gameFactory;
    }

    public void onInput(final InputAction action) {
        if (game != null && action.gameInput().isPresent()) {
            final Tuple2<Position, Position> fromTo = action.gameInput().get();
            game = game.processInput(fromTo._1, fromTo._2);
        }

        // TODO Feature: Decouple from file persistence to allow downloading over
        if (game != null && action.savePath().isPresent()) {
            final String path = action.savePath().get();
            System.err.println("[DEBUG] Saving game to " + path);
            saveLoad.saveState(game.getState(), path);
        }

        if (action.gameStateProvider().isPresent()) {
            System.err.println("[DEBUG] Game state provider provided - launching");
            game = gameFactory.createGame(action.gameStateProvider().get().createState());
        }
    }
}
