package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.vavr.collection.Map;
import io.vavr.collection.Set;

// TODO See if this is useful
public class RedundantGameState /*implements/extends GameState*/ {
    // Rationale: The basic state is enough to uniquely represent a game; this extended version helps with efficiency
    GameState basicState;
    Map<Player,Set<Position>> positionsOccupiedByPlayer;
    Map<Player, Position> kingsByPlayer;
}
