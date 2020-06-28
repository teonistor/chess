package io.github.teonistor.chess.inter;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.Validate.isTrue;

public class MultipleViewWrapper implements View {

    private final Iterable<View> views;

    private MultipleViewWrapper(Iterable<View> views) {
        this.views = views;
    }

    @Override
    public void refresh(Map<Position, Piece> board, Player player, List<Piece> capturedPieces, Position source, Set<Position> targets) {
        views.forEach(v -> v.refresh(board, player, capturedPieces, source, targets));
    }

    @Override
    public void announce(String message) {
        views.forEach(v -> v.announce(message));
    }

    public static View wrapIfNeeded(View... views) {
        isTrue(views.length > 0, "At least one view must be given");
        if (views.length == 1) {
            return requireNonNull(views[0], "All views must not be null");
        }

        final List<View> viewList = List.of(views);
        viewList.forEach(v -> requireNonNull(v, "All views must not be null"));
        return new MultipleViewWrapper(viewList);
    }
}
