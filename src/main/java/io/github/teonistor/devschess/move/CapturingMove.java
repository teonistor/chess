package io.github.teonistor.devschess.move;

import io.github.teonistor.devschess.Player;
import io.github.teonistor.devschess.board.Position;
import io.github.teonistor.devschess.piece.Piece;
import io.vavr.collection.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.function.BiFunction;
import java.util.function.Function;


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
        return board.get(to)
                .map(Piece::getPlayer)
                .filter(p -> !player.equals(p))
                .isDefined();
    }

    @Override
    public <T> T execute(Map<Position, Piece> board, Function<Map<Position, Piece>, T> nonCapturingCallback, BiFunction<Map<Position, Piece>, Piece, T> capturingCallback) {
        Piece fromPiece = board.get(from).get();
        Piece toPiece = board.get(to).get();
        return capturingCallback.apply(board.remove(from).put(to, fromPiece), toPiece);
    }
}
