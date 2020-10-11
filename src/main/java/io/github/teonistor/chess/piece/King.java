package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.move.Castle;
import io.github.teonistor.chess.move.CaptureIndependentMove;
import io.github.teonistor.chess.move.Move;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Set;

import java.util.stream.Stream;

import static io.github.teonistor.chess.board.Position.*;
import static io.github.teonistor.chess.core.Player.Black;
import static io.github.teonistor.chess.core.Player.White;
import static java.util.function.Predicate.not;


public class King extends Piece {

    private static final Map<Tuple2<Player,Position>, Set<Position>> castleTargetsByPlayerAndFrom = HashMap.of(
            new Tuple2<>(White,E1), HashSet.of(C1, G1),
            new Tuple2<>(Black,E8), HashSet.of(C8, G8));

    public King(Player player) {
        super(player);
    }

    @Override
    public Stream<Move> computePossibleMoves(Position from) {
        return Stream.concat(
               Stream.of(
                       from.up(),
                       from.left(),
                       from.right(),
                       from.down(),
                       from.up().left(),
                       from.up().right(),
                       from.down().left(),
                       from.down().right())
                   .filter(not(OutOfBoard::equals))
                   .map(to -> (Move) new CaptureIndependentMove(from, to)),

               castleTargetsByPlayerAndFrom.get(new Tuple2<>(getPlayer(), from))
                   .getOrElse(HashSet::empty)
                   .toJavaStream()
                   .map(to -> new Castle(from, to))
        );
    }
}
