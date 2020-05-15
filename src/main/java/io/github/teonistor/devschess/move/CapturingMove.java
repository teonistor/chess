package io.github.teonistor.devschess.move;

import io.github.teonistor.devschess.Board.Position;
import io.github.teonistor.devschess.Player;
import io.github.teonistor.devschess.piece.Piece;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;
import java.util.Optional;


/**
 * A move which can land on a position only if it's occupied by the opponent
 */
@AllArgsConstructor
@Getter
public class CapturingMove implements Move {

    private final @NonNull Position from;
    private final @NonNull Position to;
    private final @NonNull Player player;

    @Override
    public boolean validate(Map<Position, Piece> board) {
        return Optional.ofNullable(board.get(to))
                .map(Piece::getPlayer)
                .filter(p -> !player.equals(p))
                .isPresent();
    }

    @Override
    public void execute(Map<Position, Piece> board) {
        board.put(to, board.remove(from));
    }
}
