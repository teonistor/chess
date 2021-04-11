package io.github.teonistor.chess.util;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameStateKey;
import io.vavr.Tuple2;
import io.vavr.collection.Map;
import io.vavr.collection.Set;

import java.util.function.Function;

public class PositionPairExtractor {

    public Set<Tuple2<Position, Position>> extractBlack(final Map<GameStateKey, ?> map) {
        return extractGiven(map, key -> new Tuple2<>(key.getBlackFrom(), key.getBlackTo()));
    }

    public Set<Tuple2<Position, Position>> extractWhite(final Map<GameStateKey, ?> map) {
        return extractGiven(map, key -> new Tuple2<>(key.getWhiteFrom(), key.getWhiteTo()));
    }

    private Set<Tuple2<Position, Position>> extractGiven(final Map<GameStateKey, ?> map, final Function<GameStateKey, Tuple2<Position, Position>> extractor) {
        return map.keySet().map(extractor).filter(t -> t._1 != null);
    }
}
