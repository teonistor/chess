package io.github.teonistor.chess.testmixin;

import io.github.teonistor.chess.board.Position;
import io.vavr.collection.Iterator;
import io.vavr.collection.Stream;

public interface RandomPositionsTestMixin {
    Iterator<Position> randomPositions = Stream.continually(Stream.of(Position.values()))
            .flatMap(s -> s.splitAt(32).apply(Stream::of))
            .flatMap(Stream::shuffle).iterator();
}
