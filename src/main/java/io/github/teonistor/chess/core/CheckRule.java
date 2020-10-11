package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
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

    public boolean validate(final Map<Position, Piece> board, final Player player) {
        // TODO Dedup this line and/or store in redundant state
        final Set<Position> kingPosition = board.filterValues(new King(player)::equals).keySet();
        isTrue(kingPosition.size() == 1, "There should be exactly one %s King but found %d", player, kingPosition.size());
        return !underAttackRule.checkAttack(board, kingPosition.get(), player);
    }
}
