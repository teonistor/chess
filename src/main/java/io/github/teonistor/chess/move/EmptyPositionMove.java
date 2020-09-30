package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.Getter;

import static java.util.Objects.requireNonNull;


/**
 * A move which can land on a position only if it's empty
 */
public class EmptyPositionMove implements Move {

    private final @Getter Position from;
    private final @Getter Position to;
    private final List<Position> mustBeEmpty;

    public EmptyPositionMove(Position from, Position to, Position... additionalEmpty) {
        this.from = requireNonNull(from,"from must not be null");
        this.to = requireNonNull(to,"to must not be null");
        this.mustBeEmpty = List.of(requireNonNull(additionalEmpty, "additionalEmpty must not be null")).append(to);
    }

    @Override
    public boolean validate(GameState state) {
        return mustBeEmpty.toJavaStream()
                .map(state.getBoard()::get)
                .allMatch(Option::isEmpty);
    }

    @Override
    public GameState execute(GameState state) {
        return state.advance(state.getBoard().remove(from).put(to, state.getBoard().get(from).get()));
    }
}
