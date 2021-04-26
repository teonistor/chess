package io.github.teonistor.chess.core;

import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import io.vavr.collection.Stream;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GameStateKeyTest implements RandomPositionsTestMixin {

    private final GameStateKey a = GameStateKey.NIL.withWhiteInput(randomPositions.next(), randomPositions.next());
    private final GameStateKey b = GameStateKey.NIL.withWhitePromotion(mock(Piece.class));
    private final GameStateKey c = GameStateKey.NIL.withBlackInput(randomPositions.next(), randomPositions.next());
    private final GameStateKey d = GameStateKey.NIL.withBlackPromotion(mock(Piece.class));
    private final GameStateKey e = a.withWhitePromotion(mock(Piece.class));
    private final GameStateKey f = b.withWhiteInput(randomPositions.next(), randomPositions.next());
    private final GameStateKey g = c.withBlackPromotion(mock(Piece.class));
    private final GameStateKey h = d.withBlackInput(randomPositions.next(), randomPositions.next());

    @Test
    void matchesDefinedFieldsReflexive() {
        assertThat(a.matchesDefinedFields(a)).isTrue();
        assertThat(b.matchesDefinedFields(b)).isTrue();
        assertThat(c.matchesDefinedFields(c)).isTrue();
        assertThat(d.matchesDefinedFields(d)).isTrue();
    }

    @Test
    void doesntMatchDefinedFieldsOnDistinct() {
        Stream.of(a, b, c, d).zip(Stream.of(b, c, d, a)).forEach(keys -> assertThat(keys._1.matchesDefinedFields(keys._2)).isFalse());
    }

    @Test
    void matchesDefinedFieldsForeMoreSpecific() {
        assertThat(a.matchesDefinedFields(e)).isTrue();
        assertThat(b.matchesDefinedFields(f)).isTrue();
        assertThat(c.matchesDefinedFields(g)).isTrue();
        assertThat(d.matchesDefinedFields(h)).isTrue();
    }

    @Test
    void doesntMatchDefinedFieldsForeLessSpecific() {
        assertThat(e.matchesDefinedFields(a)).isFalse();
        assertThat(f.matchesDefinedFields(b)).isFalse();
        assertThat(g.matchesDefinedFields(c)).isFalse();
        assertThat(h.matchesDefinedFields(d)).isFalse();
    }
}