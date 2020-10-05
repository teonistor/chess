package io.github.teonistor.chess.core;

import io.vavr.collection.List;

import static io.github.teonistor.chess.core.Player.White;


public class InitialStateProvider implements GameStateProvider {

    @Override
    public GameState createState() {
        return new GameState(World.initialBoardProvider().createInitialBoard(), White, List.empty(), null);
    }
}
