package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.move.CaptureIndependentMove;
import io.github.teonistor.chess.move.Castle;
import io.github.teonistor.chess.move.Move;
import io.github.teonistor.chess.rule.UnderAttackRule;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.collection.Stream;

import static io.github.teonistor.chess.board.Position.*;
import static io.github.teonistor.chess.core.Player.Black;
import static io.github.teonistor.chess.core.Player.White;
import static java.util.function.Predicate.not;


public class King extends Piece implements AllDirectionsAwarePieceMixin {

    private static final Map<Tuple2<Player,Position>, Set<Position>> castleTargetsByPlayerAndFrom = HashMap.of(
            new Tuple2<>(White,E1), HashSet.of(C1, G1),
            new Tuple2<>(Black,E8), HashSet.of(C8, G8));

    private final UnderAttackRule underAttackRule;

    public King(final Player player, final UnderAttackRule underAttackRule) {
        super(player);
        this.underAttackRule = underAttackRule;
    }

    @Override
    public Stream<Move> computePossibleMoves(final Position from) {
        return allDirections.toStream()
             . map(step -> step.apply(from))
             . filter(not(OutOfBoard::equals))
             . map(to -> (Move) new CaptureIndependentMove(from, to))
             . appendAll(castleTargetsByPlayerAndFrom.get(new Tuple2<>(getPlayer(), from))
               . getOrElse(HashSet::empty)
               . map(to -> new Castle(from, to, underAttackRule)));
    }
}
