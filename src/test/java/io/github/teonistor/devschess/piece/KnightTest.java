package io.github.teonistor.devschess.piece;

import io.github.teonistor.devschess.board.Position;
import io.github.teonistor.devschess.Player;
import io.github.teonistor.devschess.move.CaptureIndependentMove;
import io.github.teonistor.devschess.move.Move;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static io.github.teonistor.devschess.board.Position.*;


class KnightTest {
    private final SoftAssertions assertj = new SoftAssertions();

    @ParameterizedTest
    @EnumSource(Player.class)
    void computePossibleMoves(Player player) {
        assertKnight(player, A8, C7,B6);
        assertKnight(player, D8, B7,C6,E6,F7);
        assertKnight(player, G7, E8,E6,F5,H5);
        assertKnight(player, E4, F6,G5,G3,F2,D2,C3,C5,D6);
        assertKnight(player, B1, A3,C3,D2);

        assertj.assertAll();
    }

    private void assertKnight(Player player, Position from, Position... to) {
        assertj.assertThat(new Knight(player).computePossibleMoves(from).filter(this::assertMoveType).map(Move::getTo)).containsExactlyInAnyOrder(to);
    }

    private boolean assertMoveType(Move move) {
        if (move instanceof CaptureIndependentMove) {
            return true;
        }
        throw new AssertionError("Move was not CaptureIndependentMove but " + move.getClass());
    }
}