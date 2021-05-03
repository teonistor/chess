package io.github.teonistor.chess.move;

import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.GameStateKey;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

import java.util.function.UnaryOperator;

import static java.util.function.UnaryOperator.identity;


public abstract class SingleOutcomeMove implements Move {

    /**
     * Change the mappings of positions, and other game state attributes, as needed to reflect this move taking place.
     * The validity of the move is not checked during this call.
     * @param state The current game state
     * @return The state after this move
     */
    public abstract GameState executeSingleOutcome(GameState state);

    @Override
    public Map<UnaryOperator<GameStateKey>, GameState> execute(final GameState state) {
        return HashMap.of(identity(), executeSingleOutcome(state));
    }
}
