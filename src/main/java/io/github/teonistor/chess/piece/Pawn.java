package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.move.CapturingMove;
import io.github.teonistor.chess.move.EmptyPositionMove;
import io.github.teonistor.chess.move.Move;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.stream.Stream;

import static io.github.teonistor.chess.board.Position.OutOfBoard;


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
                            new EmptyPositionMove(from, from.up()),
                            new EmptyPositionMove(from, from.up().up()),
                            new CapturingMove    (from, from.up().right()),
                            new CapturingMove    (from, from.up().left()));
                } else {
                    return Stream.of(
                            new EmptyPositionMove(from, from.up()),
                            new CapturingMove    (from, from.up().right()),
                            new CapturingMove    (from, from.up().left()));
                }

            case Black:
                if (from.toString().charAt(1) == '7') {
                    return Stream.of(
                            new EmptyPositionMove(from, from.down()),
                            new EmptyPositionMove(from, from.down().down()),
                            new CapturingMove    (from, from.down().right()),
                            new CapturingMove    (from, from.down().left()));
                } else {
                    return Stream.of(
                            new EmptyPositionMove(from, from.down()),
                            new CapturingMove    (from, from.down().right()),
                            new CapturingMove    (from, from.down().left()));
                }
        }
        throw new IllegalStateException("Should never happen");
    }
}
