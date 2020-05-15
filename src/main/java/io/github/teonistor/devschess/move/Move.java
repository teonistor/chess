package io.github.teonistor.devschess.move;

import io.github.teonistor.devschess.Board.Position;
import io.github.teonistor.devschess.piece.Piece;
import java.util.Map;


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
     * @return true if the move can be executed on the given board from the standpoint of necessary positions not being occupied/under attack; false otherwise
     */
    boolean validate(Map<Position,Piece> board);

    /**
     * Change the mappings of positions as needed to reflect this move taking place. Side-affects {@code board}. The validity of
     * the move is not checked during this call.
     *  <br> TODO if the single source of game state truth is such a {@code Map<Position,Piece>} (and the implicit assumption of {@code Position}
     *  enum values) information about captured pieces is lost. The total set of pieces cannot be assumed because pawns can promote.
     */
    void execute(Map<Position,Piece> board);
}
