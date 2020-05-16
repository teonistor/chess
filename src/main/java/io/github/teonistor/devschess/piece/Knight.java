package io.github.teonistor.devschess.piece;

import io.github.teonistor.devschess.Board.Position;
import io.github.teonistor.devschess.Player;
import io.github.teonistor.devschess.move.CaptureIndependentMove;
import io.github.teonistor.devschess.move.Move;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import java.util.stream.Stream;


@AllArgsConstructor
@Getter
public class Knight implements Piece {

    private final @NonNull Player player;

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
            .map(to -> new CaptureIndependentMove(from, to, player));
    }
}
