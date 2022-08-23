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
        return doComputePossibleMoves(from, from.up(), from.up().up(), '2', '5', '7');
    }

    private static Stream<? extends Move> computePossibleMovesBlack(final Position from) {
        return doComputePossibleMoves(from, from.down(), from.down().down(), '7', '4', '2');
    }

    private static Stream<? extends Move> doComputePossibleMoves(final Position from, final Position ahead, final Position aheadTwice, final char startingLine, final char enPassantLine, final char promotionLine) {
        final Stream<SingleOutcomeMove> common = Stream.of(
                new EmptyPositionMove(from, ahead),
                new CapturingMove(from, ahead.right()),
                new CapturingMove(from, ahead.left()));

        final char line = from.name().charAt(1);

        if (line == startingLine) return common.append(new EmptyPositionMove(from, aheadTwice, ahead));
        if (line == enPassantLine) return common.appendAll(Stream.<SingleOutcomeMove>of(
                new EnPassant(from, ahead.right(), from.right(), aheadTwice.right()),
                new EnPassant(from, ahead.left(), from.left(), aheadTwice.left())));
        if (line == promotionLine) return common.map(Promotion::new);

        return common;
    }
}
