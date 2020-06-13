package io.github.teonistor.devschess.piece;

import io.github.teonistor.devschess.Player;
import io.github.teonistor.devschess.move.CaptureIndependentMove;
import io.github.teonistor.devschess.move.LineMove;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static io.github.teonistor.devschess.board.Position.*;


class RookTest extends PieceTest{

    @ParameterizedTest
    @EnumSource(Player.class)
    void computePossibleMoves(Player player) {
        init(new Rook(player));

        assertMove(A1, CaptureIndependentMove.class,B1,A2,       LineMove.class,A3,A4,A5,A6,A7,A8,C1,D1,E1,F1,G1,H1);
        assertMove(C2, CaptureIndependentMove.class,C1,B2,C3,D2, LineMove.class,C4,C5,C6,C7,C8,A2,E2,F2,G2,H2);

        assertj.assertAll();
    }
}
