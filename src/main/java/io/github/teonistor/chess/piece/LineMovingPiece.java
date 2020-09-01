package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.move.CaptureIndependentMove;
import io.github.teonistor.chess.move.LineMove;
import io.github.teonistor.chess.move.Move;
import io.vavr.collection.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import static java.util.Objects.requireNonNull;


public abstract class LineMovingPiece extends Piece {

    private final List<UnaryOperator<Position>> steps;

    public LineMovingPiece(Player player, List<UnaryOperator<Position>> steps) {
        super(player);
        this.steps = requireNonNull(steps);
    }

    @Override
    public Stream<Move> computePossibleMoves(Position from) {
        return steps.toJavaStream().flatMap(step -> {

            final Position to = step.apply(from);
            if (to == Position.OutOfBoard) {
                return Stream.empty();
            }

            return addTilEdge(List.of(new CaptureIndependentMove(from, to)),
                    List.of(to),
                    from,
                    step.apply(to),
                    step)
                    .toJavaStream();
        });
    }

    private List<Move> addTilEdge(List<Move> moves, List<Position> mustBeEmpty, Position from, Position to, UnaryOperator<Position> step) {
        if (to == Position.OutOfBoard){
            return moves;
        }
        return addTilEdge(
                moves.prepend(new LineMove(from, to, mustBeEmpty)),
                mustBeEmpty.prepend(to),
                from,
                step.apply(to),
                step);
    }
}
