package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.King;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import static org.apache.commons.lang3.Validate.isTrue;

@AllArgsConstructor
public class CheckRule {
    private final @NonNull UnderAttackRule underAttackRule;

    public boolean validate(Map<Position, Piece> board, Player player) {
        // TODO Dedup this line and/or store in redundant state
        // TODO Change this combo to .equals() in Piece after trimming King
        final Set<Position> kingPosition = board.filterValues(piece -> piece instanceof King && piece.getPlayer() == player).keySet();
        isTrue(kingPosition.size() == 1, "There should be exactly one %s King but found %d", player, kingPosition.size());
        return !underAttackRule.checkAttack(board, kingPosition.get(), player);
    }
}
