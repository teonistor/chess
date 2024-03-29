package io.github.teonistor.chess.inter;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.collection.Traversable;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.Validate.isTrue;

public class MultipleViewWrapper implements View {

    private final Iterable<View> views;

    private MultipleViewWrapper(final Iterable<View> views) {
        this.views = views;
    }

    @Override
    public void refresh(final Map<Position, Piece> board, final Traversable<Piece> capturedPieces, final Set<Position> highlighted, final Traversable<Tuple2<Position, Position>> possibleMovesBlack, final Traversable<Tuple2<Position, Position>> possibleMovesWhite, final boolean promotionRequiredBlack, final boolean promotionRequiredWhite) {
        views.forEach(v -> v.refresh(board, capturedPieces, highlighted, possibleMovesBlack, possibleMovesWhite, promotionRequiredBlack, promotionRequiredWhite));
    }

    @Override
    public void announce(final String message) {
        views.forEach(v -> v.announce(message));
    }

    public static View wrapIfNeeded(final View... views) {
        isTrue(views.length > 0, "At least one view must be given");
        if (views.length == 1) {
            return requireNonNull(views[0], "All views must not be null");
        }

        final List<View> viewList = List.of(views);
        viewList.forEach(v -> requireNonNull(v, "All views must not be null"));
        return new MultipleViewWrapper(viewList);
    }
}
