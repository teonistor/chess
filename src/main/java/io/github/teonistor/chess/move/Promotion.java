package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.GameStateKey;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.piece.Bishop;
import io.github.teonistor.chess.piece.Knight;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.Queen;
import io.github.teonistor.chess.piece.Rook;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;
import java.util.function.UnaryOperator;

@RequiredArgsConstructor
public class Promotion implements Move {
    static final List<Function<Player, Piece>> promotables = List.of(Rook::new, Bishop::new, Knight::new, Queen::new);

    private final SingleOutcomeMove delegate;

    @Override
    public Position getFrom() {
        return delegate.getFrom();
    }

    @Override
    public Position getTo() {
        return delegate.getTo();
    }

    @Override
    public boolean validate(final GameState state) {
        return delegate.validate(state);
    }

    @Override
    public Map<UnaryOperator<GameStateKey>, GameState> execute(final GameState state) {
        return promotables
             . map(cons -> cons.apply(state.getPlayer()))
             . toMap(piece -> key -> key.withPromotion(piece),
                     piece -> delegate.executeSingleOutcome(state, piece));
    }
}
