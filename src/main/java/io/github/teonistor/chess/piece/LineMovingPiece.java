package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.move.CaptureIndependentMove;
import io.github.teonistor.chess.move.LineMove;
import io.github.teonistor.chess.move.Move;
import io.vavr.collection.List;
import io.vavr.collection.Stream;

import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;


public abstract class LineMovingPiece extends Piece {

    private final List<UnaryOperator<Position>> steps;

    protected LineMovingPiece(final Player player, final List<UnaryOperator<Position>> steps) {
        super(player);
        this.steps = requireNonNull(steps);
    }

    @Override
    public Stream<Move> computePossibleMoves(final Position from) {
        return steps.toStream().flatMap(step -> {

            final Position to = step.apply(from);
            return to == Position.OutOfBoard
                 ? Stream.empty()
                 : addTilEdge(List.of(new CaptureIndependentMove(from, to)),
                    List.of(to),
                    from,
                    step.apply(to),
                    step);
        });
    }

    private List<Move> addTilEdge(final List<Move> moves, final List<Position> mustBeEmpty, final Position from, final Position to, final UnaryOperator<Position> step) {
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
