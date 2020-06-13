package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.move.CaptureIndependentMove;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static io.github.teonistor.chess.board.Position.*;


class KnightTest extends PieceTest {

    @ParameterizedTest
    @EnumSource(Player.class)
    void computePossibleMoves(Player player) {
        init(new Knight(player));

        assertMove(A8, CaptureIndependentMove.class, C7,B6);
        assertMove(D8, CaptureIndependentMove.class, B7,C6,E6,F7);
        assertMove(G7, CaptureIndependentMove.class, E8,E6,F5,H5);
        assertMove(E4, CaptureIndependentMove.class, F6,G5,G3,F2,D2,C3,C5,D6);
        assertMove(B1, CaptureIndependentMove.class, A3,C3,D2);

        assertj.assertAll();
    }
}
