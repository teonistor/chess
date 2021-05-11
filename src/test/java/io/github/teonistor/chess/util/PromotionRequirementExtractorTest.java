package io.github.teonistor.chess.util;

import io.github.teonistor.chess.core.GameStateKey;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.piece.Rook;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import io.vavr.collection.HashMap;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PromotionRequirementExtractorTest implements RandomPositionsTestMixin {

    @Test
    void extractBlack() {
        assertThat(new PromotionRequirementExtractor().extractBlack(GameStateKey.NIL,
                HashMap.of(GameStateKey.NIL.withPromotion(new Rook(Player.Black)), null)))
                .isTrue();
        assertThat(new PromotionRequirementExtractor().extractBlack(GameStateKey.NIL,
                HashMap.of(GameStateKey.NIL.withBlackInput(randomPositions.next(), randomPositions.next()), null)))
                .isFalse();
        assertThat(new PromotionRequirementExtractor().extractBlack(GameStateKey.NIL.withPromotion(new Rook(Player.Black)),
                HashMap.of(GameStateKey.NIL.withBlackInput(randomPositions.next(), randomPositions.next()), null)))
                .isFalse();
    }

    @Test
    void testExtractBlack() {
        assertThat(new PromotionRequirementExtractor().extractWhite(GameStateKey.NIL,
                HashMap.of(GameStateKey.NIL.withPromotion(new Rook(Player.White)), null)))
                .isTrue();
        assertThat(new PromotionRequirementExtractor().extractWhite(GameStateKey.NIL,
                HashMap.of(GameStateKey.NIL.withWhiteInput(randomPositions.next(), randomPositions.next()), null)))
                .isFalse();
        assertThat(new PromotionRequirementExtractor().extractWhite(GameStateKey.NIL.withPromotion(new Rook(Player.White)),
                HashMap.of(GameStateKey.NIL.withWhiteInput(randomPositions.next(), randomPositions.next()), null)))
                .isFalse();
    }
}