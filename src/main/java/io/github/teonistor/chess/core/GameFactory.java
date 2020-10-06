package io.github.teonistor.chess.core;

import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.MultipleViewWrapper;
import io.github.teonistor.chess.inter.View;

public class GameFactory {

    public Game createGame(GameStateProvider stateProvider, Input white, Input black, View... views) {
        return new Game(stateProvider, World.checkRule(), World.gameOverChecker(), white, black, MultipleViewWrapper.wrapIfNeeded(views));
    }
}
