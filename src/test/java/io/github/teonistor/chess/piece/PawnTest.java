package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.core.UnderAttackRule;
import io.github.teonistor.chess.move.CapturingMove;
import io.github.teonistor.chess.move.EmptyPositionMove;
import io.github.teonistor.chess.move.EnPassant;
import org.junit.jupiter.api.Test;
import static io.github.teonistor.chess.board.Position.*;
import static org.mockito.Mockito.mock;


class PawnTest extends PieceTest {
    private final PieceBox box = new PieceBox(mock(UnderAttackRule.class));

    @Test
    void computePossibleMovesWhite() {
        init(box.whitePawn);

        assertMove(A2, EmptyPositionMove.class,A3,A4, CapturingMove.class,B3);
        assertMove(B5, EmptyPositionMove.class,B6,    CapturingMove.class,A6,C6, EnPassant.class, A6,C6);
        assertMove(C2, EmptyPositionMove.class,C3,C4, CapturingMove.class,B3,D3);
        assertMove(H2, EmptyPositionMove.class,H3,H4, CapturingMove.class,G3);
        assertMove(E4, EmptyPositionMove.class,E5,    CapturingMove.class,D5,F5);

        assertj.assertAll();
    }

    @Test
    void computePossibleMovesBlack() {
        init(box.blackPawn);

        assertMove(A7, EmptyPositionMove.class,A5,A6, CapturingMove.class,B6);
        assertMove(D7, EmptyPositionMove.class,D5,D6, CapturingMove.class,C6,E6);
        assertMove(H7, EmptyPositionMove.class,H5,H6, CapturingMove.class,G6);
        assertMove(G6, EmptyPositionMove.class,G5,    CapturingMove.class,F5,H5);
        assertMove(G4, EmptyPositionMove.class,G3,    CapturingMove.class,F3,H3, EnPassant.class,F3,H3);

        assertj.assertAll();
    }
}
