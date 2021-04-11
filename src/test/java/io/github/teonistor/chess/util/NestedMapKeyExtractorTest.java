package io.github.teonistor.chess.util;

import io.github.teonistor.chess.board.Position;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class NestedMapKeyExtractorTest {

    @ParameterizedTest
    @CsvSource({"D1,C4,G2,A5,H3,B8,D5,D7",
                "G7,H6,F3,E5,B6,C1,E2,A2",
                "D4,A4,G5,A6,A8,C8,G1,C7",
                "E4,A1,B4,C6,E3,G8,D2,H4"})
    void extract(final Position p1, final Position p2, final Position p3, final Position p4, final Position p5, final Position p6, final Position p7, final Position p8) {
        final NestedMapKeyExtractor extractor = new NestedMapKeyExtractor();
        final Object value = new Object();

        assertThat(extractor.extract(HashMap.of(
                p1, HashMap.of(p2, value, p3, value),
                p4, HashMap.of(p3, value),
                p5, HashMap.of(p6, value, p7, value, p8, value),
                p8, HashMap.empty())))
            .containsExactlyInAnyOrder(
                new Tuple2<>(p1, p2),
                new Tuple2<>(p1, p3),
                new Tuple2<>(p4, p3),
                new Tuple2<>(p5, p6),
                new Tuple2<>(p5, p7),
                new Tuple2<>(p5, p8));
    }
}