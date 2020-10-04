package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.InitialBoardProvider;
import io.vavr.collection.HashSet;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import static io.github.teonistor.chess.core.Player.White;


@AllArgsConstructor
public class InitialStateProvider implements GameStateProvider {
    private final @NonNull InitialBoardProvider initialBoardProvider;

    @Override
    public GameState createState() {
        return new GameState(initialBoardProvider.createInitialBoard(), White, HashSet.empty(), null);
    }
}
