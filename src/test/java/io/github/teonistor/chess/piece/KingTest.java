package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.core.UnderAttackRule;
import io.github.teonistor.chess.move.Castle;
import io.github.teonistor.chess.move.CaptureIndependentMove;
import org.junit.jupiter.api.Test;
import static io.github.teonistor.chess.board.Position.*;
import static io.github.teonistor.chess.core.Player.*;
import static org.mockito.Mockito.mock;


class KingTest extends PieceTest {

    @Test
    void computePossibleMovesWhite() {
        init(new King(White, mock(UnderAttackRule.class)));

        assertMove(A1, CaptureIndependentMove.class, A2, B1, B2);
        assertMove(H3, CaptureIndependentMove.class, H2,G2,G3,G4,H4);
        assertMove(C7, CaptureIndependentMove.class, C8,D8,D7,D6,C6,B6,B7,B8);

        assertMove(E1, CaptureIndependentMove.class,D1,D2,E2,F2,F1, Castle.class,C1,G1);
        assertj.assertAll();
    }

    @Test
    void computePossibleMovesBlack() {
        init(new King(Black, mock(UnderAttackRule.class)));

        assertMove(E8, CaptureIndependentMove.class,D8,D7,E7,F7,F8, Castle.class,C8,G8);
        assertj.assertAll();
    }
}