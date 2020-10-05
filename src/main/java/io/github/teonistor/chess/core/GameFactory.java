package io.github.teonistor.chess.core;

import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.MultipleViewWrapper;
import io.github.teonistor.chess.inter.TerminalInput;
import io.github.teonistor.chess.inter.TerminalView;
import io.github.teonistor.chess.inter.View;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import org.apache.commons.lang3.Functions.FailableFunction;

public class GameFactory {

    private final Map<StateProvision, FailableFunction<String,GameStateProvider,Exception>> stateProvision;

    public GameFactory() {
        stateProvision = HashMap.of(
                StateProvision.New,  ignore   -> World.initialStateProvider(),
                StateProvision.Load, filename -> World.saveLoad().doLoadState(filename));
    }

    public Game createTerminalGame() {
        return createGame(new TerminalInput(), new TerminalInput(), new TerminalView());
    }

    public Game createGame(Input white, Input black, View... views) {
        return new Game(white.stateProvision(stateProvision), World.checkRule(), World.gameOverChecker(), white, black, MultipleViewWrapper.wrapIfNeeded(views));
    }
}
