package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.vavr.Tuple2;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import lombok.Value;

@Value
public class InferredState {
    Map<GameStateKey,GameState> availableMoves;
    Set<Tuple2<Position, Position>> blackMovePairs;
    Set<Tuple2<Position, Position>> whiteMovePairs;
    GameCondition condition;
    Set<Position> highlighted;
    boolean promotionRequiredBlack;
    boolean promotionRequiredWhite;
}
