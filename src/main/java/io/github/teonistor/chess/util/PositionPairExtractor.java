package io.github.teonistor.chess.util;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameStateKey;
import io.vavr.Tuple2;
import io.vavr.collection.Map;
import io.vavr.collection.Set;

public class PositionPairExtractor {

    public Set<Tuple2<Position, Position>> extractBlack(final Map<GameStateKey, ?> map) {
        return map.keySet().map(key -> new Tuple2<>(key.getBlackFrom(), key.getBlackTo()));
    }

    public Set<Tuple2<Position, Position>> extractWhite(final Map<GameStateKey, ?> map) {
        return map.keySet().map(key -> new Tuple2<>(key.getWhiteFrom(), key.getWhiteTo()));
    }
}
