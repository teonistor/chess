package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.move.CapturingMove;
import io.github.teonistor.chess.move.EmptyPositionMove;
import io.github.teonistor.chess.move.EnPassant;
import io.github.teonistor.chess.move.TrailLeavingMove;
import org.junit.jupiter.api.Test;

import static io.github.teonistor.chess.core.Player.*;
import static io.github.teonistor.chess.board.Position.*;


class PawnTest extends PieceTest {

    @Test
    void computePossibleMovesWhite() {
        init(new Pawn(White));

        assertMove(A2, EmptyPositionMove.class,A3, CapturingMove.class,B3,    TrailLeavingMove.class,A4);
        assertMove(B5, EmptyPositionMove.class,B6, CapturingMove.class,A6,C6, EnPassant.class,       A6,C6);
        assertMove(C2, EmptyPositionMove.class,C3, CapturingMove.class,B3,D3, TrailLeavingMove.class,C4);
        assertMove(H2, EmptyPositionMove.class,H3, CapturingMove.class,G3,    TrailLeavingMove.class,H4);
        assertMove(E4, EmptyPositionMove.class,E5, CapturingMove.class,D5,F5);

        assertj.assertAll();
    }

    @Test
    void computePossibleMovesBlack() {
        init(new Pawn(Black));

        assertMove(A7, EmptyPositionMove.class,A6, CapturingMove.class,B6,    TrailLeavingMove.class,A5);
        assertMove(D7, EmptyPositionMove.class,D6, CapturingMove.class,C6,E6, TrailLeavingMove.class,D5);
        assertMove(H7, EmptyPositionMove.class,H6, CapturingMove.class,G6,    TrailLeavingMove.class,H5);
        assertMove(G6, EmptyPositionMove.class,G5, CapturingMove.class,F5,H5);
        assertMove(G4, EmptyPositionMove.class,G3, CapturingMove.class,F3,H3,   EnPassant.class,F3,H3);

        assertj.assertAll();
    }
}
