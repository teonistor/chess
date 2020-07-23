package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.Player;
import io.vavr.collection.List;

/**
 * A move which can land on a position if it's free or occupied by the opponent and can only be performed if certain intermediate positions are free
 */
public class LineMove extends CaptureIndependentMove {

    private final List<Position> mustBeEmpty;

    public LineMove(Position from, Position to, Player player, List<Position> mustBeEmpty) {
        super(from, to, player);
        this.mustBeEmpty = mustBeEmpty;
    }

    @Override
    public boolean validate(GameState state) {
        return super.validate(state) &&
             ! mustBeEmpty.map(state.getBoard()::containsKey).reduce(Boolean::logicalOr);
    }
}
