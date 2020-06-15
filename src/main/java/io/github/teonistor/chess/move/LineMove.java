package io.github.teonistor.chess.move;

import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.List;
import io.vavr.collection.Map;

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
    public boolean validate(Map<Position, Piece> board) {
        return super.validate(board) &&
             ! mustBeEmpty.map(board::containsKey).reduce(Boolean::logicalOr);
    }
}
