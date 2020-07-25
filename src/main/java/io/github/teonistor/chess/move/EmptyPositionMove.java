package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;


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
    public GameState execute(GameState state) {
        return state.advance(state.getBoard().remove(from).put(to, state.getBoard().get(from).get()));
    }
}
