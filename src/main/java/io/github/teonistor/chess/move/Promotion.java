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
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;
import java.util.function.UnaryOperator;

@Getter
@RequiredArgsConstructor
public class Promotion implements Move {
    static final List<Function<Player, Piece>> promotables = List.of(Rook::new, Bishop::new, Knight::new, Queen::new);

    private final @NonNull Position from;
    private final @NonNull Position to;

    @Override
    public boolean validate(final GameState state) {
        return state.getBoard().get(to).isEmpty();
    }

    @Override
    public Map<UnaryOperator<GameStateKey>, GameState> execute(final GameState state) {
        return promotables
             . map(cons -> cons.apply(state.getPlayer()))
             . toMap(piece -> key -> key.withPromotion(piece),
                     piece -> state.advance(state.getBoard().remove(from).put(to, piece)));
    }
}
