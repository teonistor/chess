package io.github.teonistor.chess.move;

import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.GameStateKey;
import io.github.teonistor.chess.piece.Piece;
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
    public GameState executeSingleOutcome(final GameState state) {
        return executeSingleOutcome(state, state.getBoard().get(getFrom()).get());
    }

    /**
     * Change the mappings of positions, and other game state attributes, as needed to reflect this move taking place.
     * The validity of the move is not checked during this call.
     * @param state The current game state
     * @param pieceToPlace What to place at the target position. This is intended for use only by other moves which delegate to this one
     * @return The state after this move
     */
    protected abstract GameState executeSingleOutcome(GameState state, Piece pieceToPlace);

    @Override
    public Map<UnaryOperator<GameStateKey>, GameState> execute(final GameState state) {
        return HashMap.of(identity(), executeSingleOutcome(state));
    }
}
