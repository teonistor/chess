package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.function.Consumer;

@AllArgsConstructor
public class PawnPromotionPartialState extends PartialState {

    private final @NonNull GameState prePromotionState;
    private final @NonNull Position promotionPosition;
    private final @NonNull Player promotingPlayer;

    @Override
    public Option<Consumer<View>> triggerViewIfNeeded() {
        return super.triggerViewIfNeeded();
    }

    @Override
    public GameState withPromotionInput(Piece piece) {

        return super.withPromotionInput(piece);
    }

    @Override
    public Map<Position, Piece> getBoard() {
        // This works for now because it's only used to determine legality of the pawn moving with respect to
        // the moving palyer's king being in check
        return prePromotionState.getBoard();
    }
}
