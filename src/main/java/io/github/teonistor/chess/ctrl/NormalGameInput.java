package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Game;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class NormalGameInput extends GameInput {

    private final Position from;
    private final Position to;

    @Override
    public Game execute(final Game game) {
        return game.processInput(from, to);
    }
}
