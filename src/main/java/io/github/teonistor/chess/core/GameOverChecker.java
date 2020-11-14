package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Tuple2;
import io.vavr.collection.Map;
import io.vavr.collection.Traversable;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import static io.github.teonistor.chess.core.GameCondition.*;
import static org.apache.commons.lang3.Validate.isTrue;

@AllArgsConstructor
public class GameOverChecker {

    private final @NonNull CheckRule checkRule;

    public GameCondition check(final Map<Position, Piece> board, final Player player, final Traversable<? extends Tuple2<?,? extends Traversable<?>>> possibleMoves) {
        if (possibleMoves.map(t -> t._2.size()).reduce(Integer::sum) == 0) {
            if (checkRule.check(board, player)) {
                switch (player) {
                    case White:
                        return BlackWins;
                    case Black:
                        return WhiteWins;
                    default:
                        throw new IllegalArgumentException("Dude wtf is this player: " + player);
                }
            } else {
                return Stalemate;
            }

        } else if (board.size() > 2) {
            return Continue;
        } else {
//            isTrue(board.size() == 2, "There should never be fewer than 2 pieces left, but found only {}", board.size());
            return Stalemate;
        }
    }
}
