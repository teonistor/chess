package io.github.teonistor.chess.util;

import io.github.teonistor.chess.core.GameStateKey;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.Map;

import java.util.function.Function;

public class PromotionRequirementExtractor {

    public boolean extractBlack(final GameStateKey key, final Map<GameStateKey, ?> map) {
        return extract(key, map, GameStateKey::getBlackPromotion);
    }

    public boolean extractWhite(final GameStateKey key, final Map<GameStateKey, ?> map) {
        return extract(key, map, GameStateKey::getWhitePromotion);
    }

    private boolean extract(GameStateKey key, Map<GameStateKey, ?> map, Function<GameStateKey, Piece> getPromotion) {
            // Ask the player what to promote to if:

            // We currently don't hold such information
        return getPromotion.apply(key) == null
            && map.keySet()

            // and some paths require it
             . filter(k -> getPromotion.apply(k) != null)

            // and we haven't diverged from such paths
             . filter(key::matchesDefinedFields)

            // and we don't need any other information besides the promotion choice (this final check really only matters in the absurdly unlikely case of a parallel game where both players promote in the same move from a hotseat session)
             . exists(k -> k.withoutPromotion().matchesDefinedFields(key));
    }
}
