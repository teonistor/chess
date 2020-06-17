package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Board;
import io.vavr.collection.HashSet;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import static io.github.teonistor.chess.core.Player.White;


@AllArgsConstructor
public class InitialStateProvider {
    private final @NonNull Board board;

    public GameState createInitialState() {
        return new GameState(board.initialSetup(), White, HashSet.empty());
    }
}
