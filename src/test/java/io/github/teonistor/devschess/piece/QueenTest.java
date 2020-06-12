package io.github.teonistor.devschess.piece;

import io.github.teonistor.devschess.Player;
import io.github.teonistor.devschess.board.Position;
import io.github.teonistor.devschess.move.Move;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static io.github.teonistor.devschess.board.Position.*;
import static java.util.stream.Collectors.toList;


class QueenTest {

    private final SoftAssertions assertj = new SoftAssertions();

    @ParameterizedTest
    @EnumSource(Player.class)
    void computePossibleMoves(Player player) {
        assertPieceMoveTargets(player, A1, A2,A3,A4,A5,A6,A7,A8,B1,C1,D1,E1,F1,G1,H1,B2,C3,D4,E5,F6,G7,H8);
        assertPieceMoveTargets(player, C2, C1,C3,C4,C5,C6,C7,C8,A2,B2,D2,E2,F2,G2,H2,A4,B3,D1,B1,D3,E4,F5,G6,H7);
        assertPieceMoveTargets(player, F4, F1,F2,F3,F5,F6,F7,F8,A4,B4,C4,D4,E4,G4,H4,B8,C7,D6,E5,G3,H2,C1,D2,E3,G5,H6);

        assertj.assertAll();
    }

    private void assertPieceMoveTargets(Player player, Position from, Position... to) {
        assertj.assertThat(new Queen(player).computePossibleMoves(from).map(Move::getTo).collect(toList())).containsExactlyInAnyOrder(to);
    }
}
