package io.github.teonistor.chess.util;

import io.vavr.Tuple2;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;

public class NestedMapKeyExtractor {

    public <K1, K2> Stream<Tuple2<K1, K2>> extract(final Map<K1, ? extends Map<K2, ?>> map) {
        return map.toStream().flatMap(m -> Stream.continually(m._1).zip(m._2.keySet()));
    }
}
