package io.github.teonistor.devschess.piece;

import io.github.teonistor.devschess.Player;
import io.github.teonistor.devschess.board.Position;
import io.github.teonistor.devschess.move.CapturingMove;
import io.github.teonistor.devschess.move.EmptyPositionMove;
import io.github.teonistor.devschess.move.Move;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.stream.Stream;

import static io.github.teonistor.devschess.board.Position.OutOfBoard;


@AllArgsConstructor
@Getter
public class Pawn implements Piece {

    private final @NonNull Player player;

    @Override
    public Stream<Move> computePossibleMoves(final Position from) {
        return computePossibleMoves0(from).filter(move -> !OutOfBoard.equals(move.getTo()));
    }

    private Stream<Move> computePossibleMoves0(final Position from) {
        switch (player) {
            case White:

                if (from.toString().charAt(1) == '2') {
                    return Stream.of(
                            new EmptyPositionMove(from, from.up(),        player),
                            new EmptyPositionMove(from, from.up().up(),   player),
                            new CapturingMove    (from, from.up().right(),player),
                            new CapturingMove    (from, from.up().left(), player));
                } else {
                    return Stream.of(
                            new EmptyPositionMove(from, from.up(),        player),
                            new CapturingMove    (from, from.up().right(),player),
                            new CapturingMove    (from, from.up().left(), player));
                }

            case Black:
                if (from.toString().charAt(1) == '7') {
                    return Stream.of(
                            new EmptyPositionMove(from, from.down(),        player),
                            new EmptyPositionMove(from, from.down().down(), player),
                            new CapturingMove    (from, from.down().right(), player),
                            new CapturingMove    (from, from.down().left(), player));
                } else {
                    return Stream.of(
                            new EmptyPositionMove(from, from.down(), player),
                            new CapturingMove    (from, from.down().right(), player),
                            new CapturingMove    (from, from.down().left(), player));
                }
        }
        throw new IllegalStateException("Should never happen");
    }
}
