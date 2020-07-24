package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.function.BiFunction;
import java.util.function.Function;


/**
 * A move which can land on a position only if it's empty
 */
@AllArgsConstructor
@Getter
public class EmptyPositionMove implements Move {

    private final @NonNull Position from;
    private final @NonNull Position to;

    @Override
    public boolean validate(GameState state) {
        return state.getBoard().get(to).isEmpty();
    }

    @Override
    public <T> T execute(Map<Position, Piece> board, Function<Map<Position, Piece>, T> nonCapturingCallback, BiFunction<Map<Position, Piece>, Piece, T> capturingCallback) {
        return nonCapturingCallback.apply(board.remove(from).put(to, board.get(from).get()));
    }
}
