package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.move.CaptureIndependentMove;
import io.github.teonistor.chess.move.LineMove;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static io.github.teonistor.chess.board.Position.*;


class BishopTest extends PieceTest{

    @ParameterizedTest
    @EnumSource(Player.class)
    void computePossibleMoves(Player player) {
        init(new Bishop(player));

        assertMove(A1, CaptureIndependentMove.class,B2,          LineMove.class,C3,D4,E5,F6,G7,H8);
        assertMove(C2, CaptureIndependentMove.class,B3,D1,B1,D3, LineMove.class,A4,E4,F5,G6,H7);
        assertMove(F4, CaptureIndependentMove.class,E3,G5,E5,G3, LineMove.class,B8,C7,D6,H2,C1,D2,H6);

        assertj.assertAll();
    }
}
