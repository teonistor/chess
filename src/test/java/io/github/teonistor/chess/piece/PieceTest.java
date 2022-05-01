package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.move.Move;
import io.vavr.Tuple2;
import org.assertj.core.api.SoftAssertions;

import java.util.ArrayList;


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
        final ArrayList<Tuple2<Position, Class<? extends Move>>> moveTypes = new ArrayList<>();

        for (Object o : to) {
            if (o instanceof Class) {
                // noinspection unchecked
                moveType = (Class<? extends Move>) o;
            } else if (o instanceof Position) {
                moveTypes.add(new Tuple2<>((Position) o, moveType));
            } else {
                throw new IllegalArgumentException("Dude wtf you gave me " + o.getClass());
            }
        }

        assertj.assertThat(piece.computePossibleMoves(from)
               .map(m -> new Tuple2<Position, Class<? extends Move>>(m.getTo(), m.getClass())))
               .containsExactlyInAnyOrderElementsOf(moveTypes);
    }
}
