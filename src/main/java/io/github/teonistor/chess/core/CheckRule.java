package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.move.Move;
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

    public boolean validateMove(Map<Position, Piece> board, Player player, Move move) {
        final Map<Position, Piece> execute = move.execute(board, b -> b, (b, ignore) -> b);
        final Set<Position> king = execute.filterValues(piece -> piece instanceof King && piece.getPlayer() == player).keySet();

        isTrue(king.size() == 1, "There should be exactly one %s King but found %d", player, king.size());

        return !underAttackRule.checkAttack(execute, king.get(), player);
    }
}
