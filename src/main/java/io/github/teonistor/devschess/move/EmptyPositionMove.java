package io.github.teonistor.devschess.move;

import io.github.teonistor.devschess.board.Position;
import io.github.teonistor.devschess.Player;
import io.github.teonistor.devschess.piece.Piece;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import java.util.Map;


/**
 * A move which can land on a position only if it's empty
 */
@AllArgsConstructor
@Getter
public class EmptyPositionMove implements Move {

    private final @NonNull Position from;
    private final @NonNull Position to;
    private final @NonNull Player player;

    @Override
    public boolean validate(Map<Position, Piece> board) {
        return board.get(to) == null;
    }

    @Override
    public void execute(Map<Position, Piece> board) {
        board.put(to, board.remove(from));
    }
}
