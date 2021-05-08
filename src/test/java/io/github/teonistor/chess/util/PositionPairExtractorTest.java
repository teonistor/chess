package io.github.teonistor.chess.util;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameStateKey;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PositionPairExtractorTest implements RandomPositionsTestMixin {

    final Position p1 = randomPositions.next();
    final Position p2 = randomPositions.next();
    final Position p3 = randomPositions.next();
    final Position p4 = randomPositions.next();
    final Position p5 = randomPositions.next();
    final Position p6 = randomPositions.next();
    final Position p7 = randomPositions.next();
    final Position p8 = randomPositions.next();

    @Test
    void extractBlack() {
        final PositionPairExtractor extractor = new PositionPairExtractor();

        assertThat(extractor.extractBlack(bigMap())).containsExactlyInAnyOrder(
                new Tuple2<>(p1, p2),
                new Tuple2<>(p1, p3),
                new Tuple2<>(p4, p3),
                new Tuple2<>(p5, p6),
                new Tuple2<>(p5, p7),
                new Tuple2<>(p5, p8));
    }

    @Test
    void extractWhite() {
        final PositionPairExtractor extractor = new PositionPairExtractor();

        assertThat(extractor.extractWhite(bigMap())).containsExactlyInAnyOrder(
                new Tuple2<>(p1, p2),
                new Tuple2<>(p4, p3),
                new Tuple2<>(p4, p5),
                new Tuple2<>(p6, p5),
                new Tuple2<>(p7, p5),
                new Tuple2<>(p8, p5));
    }

    private HashMap<GameStateKey,?> bigMap() {
        return HashMap.ofEntries(
                mockKey(white(p1, p2)),
                mockKey(white(p4, p5)),
                mockKey(white(p4, p3)),
                mockKey(white(p6, p5)),
                mockKey(white(p7, p5)),
                mockKey(white(p8, p5)),
                mockKey(black(p1, p2)),
                mockKey(black(p1, p3)),
                mockKey(black(p4, p3)),
                mockKey(black(p5, p6)),
                mockKey(black(p5, p7)),
                mockKey(black(p5, p8)));
    }

    private Tuple2<GameStateKey, ?> mockKey(final Consumer<GameStateKey> stubs) {
        final GameStateKey key = mock(GameStateKey.class);
        stubs.accept(key);
        return new Tuple2<>(key, new Object());
    }

    private Consumer<GameStateKey> black(final Position from, final Position to) {
        return key -> {
            when(key.getBlackFrom()).thenReturn(from);
            when(key.getBlackTo()).thenReturn(to);
        };
    }

    private Consumer<GameStateKey> white(final Position from, final Position to) {
        return key -> {
            when(key.getWhiteFrom()).thenReturn(from);
            when(key.getWhiteTo()).thenReturn(to);
        };
    }
}