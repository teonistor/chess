package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.move.CaptureIndependentMove;
import io.github.teonistor.chess.move.Move;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public class King implements Piece {

    private final @NonNull Player player;

    @Override
    public Stream<Move> computePossibleMoves(Position from) {
        return computePossibleTargets(from)
              .map(to -> (Move) new CaptureIndependentMove(from, to))
              .toJavaStream();
    }

    protected io.vavr.collection.Stream<Position> computePossibleTargets(Position from) {
        return io.vavr.collection.Stream.of(
                from.up(),
                from.left(),
                from.right(),
                from.down(),
                from.up().left(),
                from.up().right(),
                from.down().left(),
                from.down().right())
            .filter(to -> !Position.OutOfBoard.equals(to));
    }
}
