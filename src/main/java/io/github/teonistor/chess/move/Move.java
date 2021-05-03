package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.GameStateKey;
import io.vavr.collection.Map;

import java.util.function.UnaryOperator;

public interface Move {

    /**
     * @return The position from where the current player will "pick up" their piece to begin the move
     */
    Position getFrom();

    /**
     * @return The position, not necessarily empty, where the "picked up" piece, which is not necessarily the only piece to
     * move during the move, will "land"
     */
    Position getTo();

    /**
     * Check if the move can be executed on the current board from the standpoint of necessary positions not being occupied/under attack etc
     * @param state The current game state
     * @return true if the move can be executed; false otherwise
     */
    boolean validate(GameState state);

    /**
     * Change the mappings of positions, and other game state attributes, as needed to reflect this move taking place.
     * The validity of the move is not checked during this call.
     * @param state The current game state
     * @return One or more states after this move, keyed on operators possibly adding additional input to a key
     */
    Map<UnaryOperator<GameStateKey>, GameState> execute(GameState state);
}
