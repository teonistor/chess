package io.github.teonistor.chess.util;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameStateKey;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import org.junit.jupiter.api.Test;

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
        final Object value = new Object();

        assertThat(extractor.extractBlack(HashMap.of(
                mockKeyBlack(p1, p2), value,
                mockKeyBlack(p1, p3), value,
                mockKeyBlack(p4, p3), value,
                mockKeyBlack(p5, p6), value,
                mockKeyBlack(p5, p7), value,
                mockKeyBlack(p5, p8), value)))
            .containsExactlyInAnyOrder(
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
        final Object value = new Object();

        assertThat(extractor.extractWhite(HashMap.of(
                mockKeyWhite(p1, p2), value,
                mockKeyWhite(p4, p5), value,
                mockKeyWhite(p4, p3), value,
                mockKeyWhite(p6, p5), value,
                mockKeyWhite(p7, p5), value,
                mockKeyWhite(p8, p5), value)))
            .containsExactlyInAnyOrder(
                new Tuple2<>(p1, p2),
                new Tuple2<>(p4, p3),
                new Tuple2<>(p4, p5),
                new Tuple2<>(p6, p5),
                new Tuple2<>(p7, p5),
                new Tuple2<>(p8, p5));
    }

    private GameStateKey mockKeyBlack(Position from, Position to) {
        final GameStateKey key = mock(GameStateKey.class);
        when(key.getBlackFrom()).thenReturn(from);
        when(key.getBlackTo()).thenReturn(to);
        return key;
    }

    private GameStateKey mockKeyWhite(Position from, Position to) {
        final GameStateKey key = mock(GameStateKey.class);
        when(key.getWhiteFrom()).thenReturn(from);
        when(key.getWhiteTo()).thenReturn(to);
        return key;
    }
}