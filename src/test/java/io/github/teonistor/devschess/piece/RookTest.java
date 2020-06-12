package io.github.teonistor.devschess.piece;

import io.github.teonistor.devschess.Player;
import io.github.teonistor.devschess.board.Position;
import io.github.teonistor.devschess.move.Move;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import javax.validation.Valid;

import static io.github.teonistor.devschess.board.Position.*;
import static java.util.stream.Collectors.toList;


class RookTest {

    private final SoftAssertions assertj = new SoftAssertions();

    @ParameterizedTest
    @EnumSource(Player.class)
    void computePossibleMoves(Player player) {
        assertRook(player, A1, A2,A3,A4,A5,A6,A7,A8,B1,C1,D1,E1,F1,G1,H1);
        assertRook(player, C2, C1,C3,C4,C5,C6,C7,C8,A2,B2,D2,E2,F2,G2,H2);

        assertj.assertAll();
    }

    private void assertRook(Player player, Position from, Position... to) {
        assertj.assertThat(new Rook(player).computePossibleMoves(from).map(Move::getTo).collect(toList())).containsExactlyInAnyOrder(to);
    }
}
