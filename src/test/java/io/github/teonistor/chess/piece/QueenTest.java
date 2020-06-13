package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.move.CaptureIndependentMove;
import io.github.teonistor.chess.move.LineMove;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static io.github.teonistor.chess.board.Position.*;


class QueenTest extends PieceTest {

    @ParameterizedTest
    @EnumSource(Player.class)
    void computePossibleMoves(Player player) {
        init(new Queen(player));

        assertMove(A1, CaptureIndependentMove.class, A2,B1,B2,                LineMove.class, A3,A4,A5,A6,A7,A8,C1,D1,E1,F1,G1,H1,C3,D4,E5,F6,G7,H8);
        assertMove(C2, CaptureIndependentMove.class, B1,C1,D1,B2,D2,B3,D3,C3, LineMove.class, C4,C5,C6,C7,C8,A2,E2,F2,G2,H2,A4,E4,F5,G6,H7);
        assertMove(F4, CaptureIndependentMove.class, E3,F3,G3,E4,G4,E5,F5,G5, LineMove.class, F1,F2,F6,F7,F8,A4,B4,C4,D4,H4,B8,C7,D6,H2,C1,D2,H6);
        assertMove(A6, CaptureIndependentMove.class, A5,B5,B6,A7,B7,          LineMove.class, A1,A2,A3,A4,A8,F1,E2,D3,C4,C6,D6,E6,F6,G6,H6,C8);

        assertj.assertAll();
    }
}
