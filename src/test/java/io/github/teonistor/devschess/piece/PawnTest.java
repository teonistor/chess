package io.github.teonistor.devschess.piece;

import io.github.teonistor.devschess.board.Position;
import io.github.teonistor.devschess.Player;
import io.github.teonistor.devschess.move.Move;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static io.github.teonistor.devschess.board.Position.*;
import static io.github.teonistor.devschess.Player.*;
import static java.util.stream.Collectors.toList;


class PawnTest {

    private final SoftAssertions assertj = new SoftAssertions();

    @Test
    void computePossibleMoves() {
        assertPawn(White, A2, A3, A4, B3);
        assertPawn(White, C2, B3, C3, C4, D3);
        assertPawn(White, H2, G3, H3, H4);
        assertPawn(White, E4, D5, E5, F5);

        assertPawn(Black, A7, A6, A5, B6);
        assertPawn(Black, D7, C6, D6, D5, E6);
        assertPawn(Black, H7, G6, H6, H5);
        assertPawn(Black, G6, F5, G5, H5);

        assertj.assertAll();
    }

    private void assertPawn(Player player, Position from, Position... to) {
        assertj.assertThat(new Pawn(player).computePossibleMoves(from).map(Move::getTo).collect(toList())).containsExactlyInAnyOrder(to);
    }
}
