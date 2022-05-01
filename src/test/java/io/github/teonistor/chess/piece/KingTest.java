package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.move.CaptureIndependentMove;
import io.github.teonistor.chess.move.Castle;
import io.github.teonistor.chess.rule.UnderAttackRule;
import org.junit.jupiter.api.Test;

import static io.github.teonistor.chess.board.Position.A1;
import static io.github.teonistor.chess.board.Position.A2;
import static io.github.teonistor.chess.board.Position.B1;
import static io.github.teonistor.chess.board.Position.B2;
import static io.github.teonistor.chess.board.Position.B6;
import static io.github.teonistor.chess.board.Position.B7;
import static io.github.teonistor.chess.board.Position.B8;
import static io.github.teonistor.chess.board.Position.C1;
import static io.github.teonistor.chess.board.Position.C6;
import static io.github.teonistor.chess.board.Position.C7;
import static io.github.teonistor.chess.board.Position.C8;
import static io.github.teonistor.chess.board.Position.D1;
import static io.github.teonistor.chess.board.Position.D2;
import static io.github.teonistor.chess.board.Position.D6;
import static io.github.teonistor.chess.board.Position.D7;
import static io.github.teonistor.chess.board.Position.D8;
import static io.github.teonistor.chess.board.Position.E1;
import static io.github.teonistor.chess.board.Position.E2;
import static io.github.teonistor.chess.board.Position.E7;
import static io.github.teonistor.chess.board.Position.E8;
import static io.github.teonistor.chess.board.Position.F1;
import static io.github.teonistor.chess.board.Position.F2;
import static io.github.teonistor.chess.board.Position.F7;
import static io.github.teonistor.chess.board.Position.F8;
import static io.github.teonistor.chess.board.Position.G1;
import static io.github.teonistor.chess.board.Position.G2;
import static io.github.teonistor.chess.board.Position.G3;
import static io.github.teonistor.chess.board.Position.G4;
import static io.github.teonistor.chess.board.Position.G8;
import static io.github.teonistor.chess.board.Position.H2;
import static io.github.teonistor.chess.board.Position.H3;
import static io.github.teonistor.chess.board.Position.H4;
import static io.github.teonistor.chess.core.Player.Black;
import static io.github.teonistor.chess.core.Player.White;
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