package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.move.CaptureIndependentMove;
import io.github.teonistor.chess.move.Move;
import io.vavr.collection.Stream;


public class Knight extends Piece {

    public Knight(Player player) {
        super(player);
    }

    @Override
    public Stream<Move> computePossibleMoves(Position from) {
        return Stream.of(
                from.up().up().left(),
                from.up().up().right(),
                from.left().left().up(),
                from.left().left().down(),
                from.down().down().left(),
                from.down().down().right(),
                from.right().right().up(),
                from.right().right().down())
            .filter(to -> !Position.OutOfBoard.equals(to))
            .map(to -> new CaptureIndependentMove(from, to));
    }
}
