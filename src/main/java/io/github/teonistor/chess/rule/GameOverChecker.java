package io.github.teonistor.chess.rule;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameCondition;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.Map;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import static io.github.teonistor.chess.core.GameCondition.BlackWins;
import static io.github.teonistor.chess.core.GameCondition.Continue;
import static io.github.teonistor.chess.core.GameCondition.Stalemate;
import static io.github.teonistor.chess.core.GameCondition.WhiteWins;

@AllArgsConstructor
public class GameOverChecker {

    private final @NonNull CheckRule checkRule;

    public GameCondition check(final Map<Position, Piece> board, final Player player, final Map<?, ?> possibleMoves) {
        if (possibleMoves.size() == 0) {
            if (checkRule.check(board, player))
                switch (player) {
                    case White:
                        return BlackWins;
                    case Black:
                        return WhiteWins;
                    default:
                        throw new IllegalArgumentException("Dude wtf is this player: " + player);
                }
            return Stalemate;
        }

        if (board.size() > 2)
            return Continue;

        return Stalemate;
    }
}
