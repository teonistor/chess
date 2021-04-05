package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.Map;
import io.vavr.control.Option;

import java.util.function.Consumer;

public abstract class PartialState {

    public Option<GameState> completeState() {
        return Option.none();
    }

    public Option<Consumer<View>> triggerViewIfNeeded() {
        return Option.none();
    }

    public GameState withPromotionInput(final Piece piece) {
        throw new UnsupportedOperationException("PartialState::withPromotionInput");
    }

    public Map<Position, Piece> getBoard() {
        throw new UnsupportedOperationException("PartialState::getBoard");
    }

    public static PartialState NIL = new PartialState() {};
}
