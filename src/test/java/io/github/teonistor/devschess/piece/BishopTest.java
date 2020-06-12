package io.github.teonistor.devschess.piece;

import io.github.teonistor.devschess.Player;
import io.github.teonistor.devschess.board.Position;
import io.github.teonistor.devschess.move.Move;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static io.github.teonistor.devschess.board.Position.*;
import static java.util.stream.Collectors.toList;


class BishopTest {

    private final SoftAssertions assertj = new SoftAssertions();

    @ParameterizedTest
    @EnumSource(Player.class)
    void computePossibleMoves(Player player) {
        assertBishop(player, A1, B2,C3,D4,E5,F6,G7,H8);
        assertBishop(player, C2, A4,B3,D1,B1,D3,E4,F5,G6,H7);
        assertBishop(player, F4, B8,C7,D6,E5,G3,H2,C1,D2,E3,G5,H6);

        assertj.assertAll();
    }

    private void assertBishop(Player player, Position from, Position... to) {
        assertj.assertThat(new Bishop(player).computePossibleMoves(from).map(Move::getTo).collect(toList())).containsExactlyInAnyOrder(to);
    }
}
