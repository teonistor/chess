package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.move.Move;
import io.vavr.collection.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;

@AllArgsConstructor
public abstract class Piece {
    private @Getter @NonNull Player player;

    public abstract Stream<Move> computePossibleMoves(Position from);

    @Override
    public boolean equals(final Object o) {
        return this == o || o != null && getClass() == o.getClass() && player == ((Piece) o).player;
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, getClass());
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", getClass().getName(), player);
    }
}
