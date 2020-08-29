package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.InitialBoardProvider;
import io.vavr.collection.HashSet;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import static io.github.teonistor.chess.core.Player.White;


@AllArgsConstructor
public class InitialStateProvider {
    private final @NonNull InitialBoardProvider initialBoardProvider;

    public GameState createInitialState() {
        return new GameState(initialBoardProvider.createInitialBoard(), White, HashSet.empty(), null);
    }
}
