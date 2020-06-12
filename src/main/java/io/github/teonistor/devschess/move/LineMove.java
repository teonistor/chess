package io.github.teonistor.devschess.move;

import io.github.teonistor.devschess.Player;
import io.github.teonistor.devschess.board.Position;
import io.github.teonistor.devschess.piece.Piece;
import io.vavr.collection.List;

import java.util.Map;

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
