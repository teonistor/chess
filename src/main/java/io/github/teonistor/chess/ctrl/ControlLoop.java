package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.core.GameData;
import io.github.teonistor.chess.factory.GameFactory;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.save.SaveLoad;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.io.OutputStream;

@RequiredArgsConstructor
public class ControlLoop {

    private final SaveLoad saveLoad;
    private final GameFactory gameFactory;
    private final View view;

    // The only allowed mutable state
    private Game game;

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

    public void loadGame(final InputStream inputStream) {
        final GameData data = saveLoad.load(inputStream);

    }

    public void saveGame(final OutputStream outputStream) {
        saveLoad.save(new GameData(game.getType(), game.getState()), outputStream);
    }

    private void setAndTrigger(final Game game) {
        this.game = game;
        game.triggerView(view);
    }
}
