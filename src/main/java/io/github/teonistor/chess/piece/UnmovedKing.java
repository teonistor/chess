package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.core.UnderAttackRule;
import io.github.teonistor.chess.move.Castle;
import io.github.teonistor.chess.move.KingsFirstMove;
import io.github.teonistor.chess.move.Move;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Set;

import java.util.stream.Stream;

import static io.github.teonistor.chess.board.Position.C1;
import static io.github.teonistor.chess.board.Position.C8;
import static io.github.teonistor.chess.board.Position.G1;
import static io.github.teonistor.chess.board.Position.G8;
import static io.github.teonistor.chess.core.Player.Black;
import static io.github.teonistor.chess.core.Player.White;

public class UnmovedKing extends King {

    private static final Map<Player,Set<Position>> castleTargetsByPlayer = HashMap.of(
            White, HashSet.of(C1, G1),
            Black, HashSet.of(C8, G8));

    private final UnderAttackRule underAttackRule;

    public UnmovedKing(final Player player, UnderAttackRule underAttackRule) {
        super(player);
        this.underAttackRule = underAttackRule;
    }

    @Override
    public Stream<Move> computePossibleMoves(Position from) {
        return computePossibleTargets(from)
              .map(to -> (Move) new KingsFirstMove(from, to))
              .appendAll(castleTargetsByPlayer.get(getPlayer()).get().toStream()
              .map(to -> new Castle(from, to, underAttackRule)))
              .toJavaStream();
    }
}
