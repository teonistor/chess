package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.move.CapturingMove;
import io.github.teonistor.chess.move.EmptyPositionMove;
import io.github.teonistor.chess.move.EnPassant;
import io.github.teonistor.chess.move.Promotion;
import io.github.teonistor.chess.rule.UnderAttackRule;
import org.junit.jupiter.api.Test;

import static io.github.teonistor.chess.board.Position.A1;
import static io.github.teonistor.chess.board.Position.A2;
import static io.github.teonistor.chess.board.Position.A3;
import static io.github.teonistor.chess.board.Position.A4;
import static io.github.teonistor.chess.board.Position.A5;
import static io.github.teonistor.chess.board.Position.A6;
import static io.github.teonistor.chess.board.Position.A7;
import static io.github.teonistor.chess.board.Position.B1;
import static io.github.teonistor.chess.board.Position.B2;
import static io.github.teonistor.chess.board.Position.B3;
import static io.github.teonistor.chess.board.Position.B5;
import static io.github.teonistor.chess.board.Position.B6;
import static io.github.teonistor.chess.board.Position.C1;
import static io.github.teonistor.chess.board.Position.C2;
import static io.github.teonistor.chess.board.Position.C3;
import static io.github.teonistor.chess.board.Position.C4;
import static io.github.teonistor.chess.board.Position.C6;
import static io.github.teonistor.chess.board.Position.C8;
import static io.github.teonistor.chess.board.Position.D3;
import static io.github.teonistor.chess.board.Position.D5;
import static io.github.teonistor.chess.board.Position.D6;
import static io.github.teonistor.chess.board.Position.D7;
import static io.github.teonistor.chess.board.Position.D8;
import static io.github.teonistor.chess.board.Position.E4;
import static io.github.teonistor.chess.board.Position.E5;
import static io.github.teonistor.chess.board.Position.E6;
import static io.github.teonistor.chess.board.Position.E8;
import static io.github.teonistor.chess.board.Position.F3;
import static io.github.teonistor.chess.board.Position.F5;
import static io.github.teonistor.chess.board.Position.G3;
import static io.github.teonistor.chess.board.Position.G4;
import static io.github.teonistor.chess.board.Position.G5;
import static io.github.teonistor.chess.board.Position.G6;
import static io.github.teonistor.chess.board.Position.H2;
import static io.github.teonistor.chess.board.Position.H3;
import static io.github.teonistor.chess.board.Position.H4;
import static io.github.teonistor.chess.board.Position.H5;
import static io.github.teonistor.chess.board.Position.H6;
import static io.github.teonistor.chess.board.Position.H7;
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
        assertMove(D7, Promotion.class, C8, D8, E8);

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
        assertMove(B2, Promotion.class, A1, B1, C1);

        assertj.assertAll();
    }
}
