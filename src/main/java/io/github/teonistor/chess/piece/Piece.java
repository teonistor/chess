package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.move.Move;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;
import java.util.stream.Stream;

@AllArgsConstructor
public abstract class Piece {
    private @Getter @NonNull Player player;

    public abstract Stream<Move> computePossibleMoves(Position from);

    @Override
    public boolean equals(Object o) {
        return this == o || o != null && getClass() == o.getClass() && player == ((Piece) o).player;
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, getClass());
    }
}
