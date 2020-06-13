package io.github.teonistor.devschess.piece;

import io.github.teonistor.devschess.move.CapturingMove;
import io.github.teonistor.devschess.move.EmptyPositionMove;
import org.junit.jupiter.api.Test;

import static io.github.teonistor.devschess.Player.*;
import static io.github.teonistor.devschess.board.Position.*;


class PawnTest extends PieceTest {

    @Test
    void computePossibleMovesWhite() {
        init(new Pawn(White));

        assertMove(A2, EmptyPositionMove.class,A3,A4, CapturingMove.class,B3);
        assertMove(C2, EmptyPositionMove.class,C3,C4, CapturingMove.class,B3,D3);
        assertMove(H2, EmptyPositionMove.class,H3,H4, CapturingMove.class,G3);
        assertMove(E4, EmptyPositionMove.class,E5,    CapturingMove.class,D5,F5);

        assertj.assertAll();
    }

    @Test
    void computePossibleMovesBlack() {
        init(new Pawn(Black));

        assertMove(A7, EmptyPositionMove.class,A6,A5, CapturingMove.class,B6);
        assertMove(D7, EmptyPositionMove.class,D6,D5, CapturingMove.class,C6,E6);
        assertMove(H7, EmptyPositionMove.class,H6,H5, CapturingMove.class,G6);
        assertMove(G6, EmptyPositionMove.class,G5,    CapturingMove.class,F5,H5);

        assertj.assertAll();
    }
}
