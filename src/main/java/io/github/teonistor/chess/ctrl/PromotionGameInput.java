package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.piece.Piece;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class PromotionGameInput extends GameInput {

    private final Piece piece;

    @Override
    public Game execute(final Game game) {
        return game.processInput(piece);
    }
}
