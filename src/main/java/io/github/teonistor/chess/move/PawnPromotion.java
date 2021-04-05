package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.PartialState;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PawnPromotion implements Move {

    @Override
    public Position getFrom() {
        return wrapped.getFrom();
    }

    @Override
    public Position getTo() {
        return wrapped.getTo();
    }

    @Override
    public boolean validate(GameState state) {
        return wrapped.validate(state);
    }

    @Override
    public PartialState execute(GameState state) {
        return wrapped.execute(state);
    }

    private final Move wrapped;


}
