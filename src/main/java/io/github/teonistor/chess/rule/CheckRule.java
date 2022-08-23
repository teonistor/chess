package io.github.teonistor.chess.rule;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.piece.King;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import static org.apache.commons.lang3.Validate.isTrue;

@AllArgsConstructor
public class CheckRule {
    private final @NonNull UnderAttackRule underAttackRule;

    /**
     * Check whether the given player is under check
     * @param board the board to check
     * @param player the player to check
     * @return true if the given player is under check, false otherwise
     * @throws IllegalArgumentException if there is not exactly one king of the given player on the board
     */
    public boolean check(final Map<Position, Piece> board, final Player player) {
        final Set<Position> kingPosition = board.filterValues(piece -> piece instanceof King && piece.getPlayer() == player).keySet();
        isTrue(kingPosition.size() == 1, "There should be exactly one %s King but found %d", player, kingPosition.size());
        return underAttackRule.checkAttack(board, kingPosition.get(), player);
    }
}
