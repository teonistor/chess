package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.factory.GameFactory;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.save.SaveLoad;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

import static io.github.teonistor.chess.factory.Factory.GameType.STANDARD;

@RequiredArgsConstructor
public class ControlLoop {

    private final SaveLoad saveLoad;
    private final GameFactory gameFactory;
    private final View view;

    // The only allowed mutable state
    private Game game;

    @Deprecated
    public void onInput(final InputAction action) {
        if (game != null && action.gameInput().isPresent()) {
            final Tuple2<Position, Position> fromTo = action.gameInput().get();
            setAndTrigger(game.processInput(fromTo._1, fromTo._2));
        }

        // TODO Feature: Decouple from file persistence to allow downloading over
        if (game != null && action.savePath().isPresent()) {
            final String path = action.savePath().get();
            System.err.println("[DEBUG] Saving game to " + path);
            saveLoad.saveState(game.getState(), path);
        }

        if (action.gameStateProvider().isPresent()) {
            System.err.println("[DEBUG] Game state provider provided - launching");
            setAndTrigger(gameFactory.createGame(STANDARD, action.gameStateProvider().get().createState()));
        }
    }

    public void gameInput(final GameInput input) {
        if (game != null) {
            setAndTrigger(input.execute(game));
        }
    }

    public void newStandardGame() {
        setAndTrigger(gameFactory.createNewStandardGame());
    }

    public void newParallelGame() {
        setAndTrigger(gameFactory.createNewParallelGame());
    }

    public void loadGame(final byte[] data) {

    }

    public void saveGame(final Consumer<byte[]> dataSink) {

    }

    private void setAndTrigger(final Game game) {
        this.game = game;
        game.triggerView(view);
    }
}
