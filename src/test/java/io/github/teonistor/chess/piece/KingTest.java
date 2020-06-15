package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.move.CaptureIndependentMove;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static io.github.teonistor.chess.board.Position.*;


class KingTest extends PieceTest {

    @ParameterizedTest
    @EnumSource(Player.class)
    void computePossibleMoves(Player player) {
        init(new King(player));

        assertMove(A1, CaptureIndependentMove.class, A2, B1, B2);
        assertMove(H3, CaptureIndependentMove.class, H2,G2,G3,G4,H4);
        assertMove(C7, CaptureIndependentMove.class, C8,D8,D7,D6,C6,B6,B7,B8);

        assertj.assertAll();
    }
}