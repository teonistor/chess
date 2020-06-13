package io.github.teonistor.chess.move;

import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.function.BiFunction;
import java.util.function.Function;


/**
 * A move which can land on a position if it's free or occupied by the opponent
 */
@AllArgsConstructor
@Getter
public class CaptureIndependentMove implements Move {

    private final @NonNull Position from;
    private final @NonNull Position to;
    private final @NonNull Player player;

    @Override
    public boolean validate(Map<Position, Piece> board) {
        return !board.get(to)
                .map(Piece::getPlayer)
                .filter(player::equals)
                .isDefined();
    }

    @Override
    public <T> T execute(Map<Position, Piece> board, Function<Map<Position, Piece>, T> nonCapturingCallback, BiFunction<Map<Position, Piece>, Piece, T> capturingCallback) {
        Piece fromPiece = board.get(from).get();
        Option<Piece> toPiece = board.get(to);
        if (toPiece.isDefined()) {
            return capturingCallback.apply(board.remove(from).put(to, fromPiece), toPiece.get());
        } else {
            return nonCapturingCallback.apply(board.remove(from).put(to, fromPiece));
        }
    }
}
