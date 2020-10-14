package io.github.teonistor.chess.core;

public class GameFactory {

    public Game createGame(final GameStateProvider stateProvider) {
        return new Game(stateProvider);
    }
}
