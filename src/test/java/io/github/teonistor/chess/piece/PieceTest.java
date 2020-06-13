package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.move.Move;
import io.vavr.collection.List;
import org.assertj.core.api.SoftAssertions;

import java.util.HashMap;


public abstract class PieceTest {

    protected SoftAssertions assertj;
    protected Piece piece;

    protected void init (Piece piece) {
        assertj = new SoftAssertions();
        this.piece = piece;
    }

    // This verification method is rather complicated
    // But it successfully allows piece tests to test move targets and their types with minimal cruft
    protected void assertMove(Position from, Class<? extends Move> moveType, final Object...to) {
        final HashMap<Position, Class<? extends Move>> moveTypes = new java.util.HashMap<>();

        for (Object o : to) {
            if (o instanceof Class) {
                moveType = (Class<? extends Move>) o;
            } else if (o instanceof Position) {
                moveTypes.put((Position) o, moveType);
            } else {
                throw new IllegalArgumentException("Dude wtf you gave me " + o.getClass());
            }
        }

        List<Move> actualMoves = List.ofAll(piece.computePossibleMoves(from));
        assertj.assertThat(actualMoves)
               .allSatisfy(m -> assertj.assertThat(moveTypes.get(m.getTo())).isEqualTo(m.getClass()));
        assertj.assertThat(actualMoves.map(Move::getTo))
               .containsExactlyInAnyOrderElementsOf(moveTypes.keySet());
    }
}
