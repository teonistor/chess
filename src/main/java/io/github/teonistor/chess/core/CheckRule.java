package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.move.Move;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.Map;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class CheckRule {
    private final @NonNull UnderAttackRule underAttackRule;

    public boolean validateMove(Map<Position, Piece> board, Player player, Move move) {
        return true;
    }
}
