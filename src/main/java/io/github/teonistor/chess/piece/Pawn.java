package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.move.CapturingMove;
import io.github.teonistor.chess.move.EmptyPositionMove;
import io.github.teonistor.chess.move.EnPassant;
import io.github.teonistor.chess.move.Move;
import io.github.teonistor.chess.move.Promotion;
import io.github.teonistor.chess.move.SingleOutcomeMove;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;

import java.util.function.BiFunction;
import java.util.function.Function;

import static io.github.teonistor.chess.board.Position.OutOfBoard;


public class Pawn extends Piece {
    private static final Map<Player, Function<Position, Stream<? extends Move>>> computeBasedOnPlayer = HashMap.of(
            Player.White, Pawn::computePossibleMovesWhite,
            Player.Black, Pawn::computePossibleMovesBlack);

    public Pawn(final Player player) {
        super(player);
    }

    @Override
    public Stream<Move> computePossibleMoves(final Position from) {
        return Stream.narrow(Stream.of(getPlayer())
             . map(computeBasedOnPlayer)
             . flatMap(computation -> computation.apply(from))
             . filter(move -> !OutOfBoard.equals(move.getTo())));
    }

    private static Stream<? extends Move> computePossibleMovesWhite(final Position from) {
        final Stream<SingleOutcomeMove> common = Stream.of(
                new EmptyPositionMove(from, from.up()),
                new CapturingMove(from, from.up().right()),
                new CapturingMove(from, from.up().left()));

        final SingleOutcomeMove startingExtra = new EmptyPositionMove(from, from.up().up(), from.up());

        final Stream<SingleOutcomeMove> enPassantExtra = Stream.of(
                new EnPassant(from, from.up().right(), from.right(), from.up().up().right()),
                new EnPassant(from, from.up().left(), from.left(), from.up().up().left()));

        final char line = from.toString().charAt(1);

        if (line == '2') return common.append(startingExtra);
        if (line == '5') return common.appendAll(enPassantExtra);
        if (line == '7') return common.map(Promotion::new);
        return common;
    }

    private static Stream<? extends Move> computePossibleMovesBlack(final Position from) {
        final Stream<SingleOutcomeMove> common = Stream.of(
                new EmptyPositionMove(from, from.down()),
                new CapturingMove(from, from.down().right()),
                new CapturingMove(from, from.down().left()));

        final char line = from.toString().charAt(1);

        BiFunction<Stream<? extends SingleOutcomeMove>, Character, Stream<? extends  Move> > ff;

        if (line == '7') return common.append(new EmptyPositionMove(from, from.down().down(), from.down()));
        if (line == '4') return common.appendAll(Stream.<SingleOutcomeMove>of(
                        new EnPassant(from, from.down().right(), from.right(), from.down().down().right()),
                        new EnPassant(from, from.down().left(), from.left(), from.down().down().left())));
        if (line == '2') return common.map(Promotion::new);

        return common;
    }

}
