package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.Map;

import java.util.function.BiFunction;
import java.util.function.Function;


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
     * Check if the move can be executed on the given board from the standpoint of necessary positions not being occupied/under attack etc
     * @param board The current game board
     * @return true if the move can be executed; false otherwise
     */
    boolean validate(Map<Position,Piece> board);

    /**
     * Change the mappings of positions as needed to reflect this move taking place.
     * The validity of the move is not checked during this call.
     * @param board The current game board
     * @param nonCapturingCallback Callback called with the new board when the move doesn't incur capturing
     * @param capturingCallback Callback called with the new board and the captured piece when capturing occurs
     * @param <T> A type that the caller needs
     * @return What the chosen callback returns
     */
    <T> T execute(Map<Position,Piece> board, Function<Map<Position,Piece>,T> nonCapturingCallback, BiFunction<Map<Position,Piece>,Piece,T> capturingCallback);
}
