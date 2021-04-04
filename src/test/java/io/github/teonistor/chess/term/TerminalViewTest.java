package io.github.teonistor.chess.term;

import io.github.teonistor.chess.core.UnderAttackRule;
import io.github.teonistor.chess.piece.Bishop;
import io.github.teonistor.chess.piece.Knight;
import io.github.teonistor.chess.piece.Pawn;
import io.github.teonistor.chess.piece.PieceBox;
import io.github.teonistor.chess.piece.Rook;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import static io.github.teonistor.chess.board.Position.*;
import static io.github.teonistor.chess.core.Player.Black;
import static io.github.teonistor.chess.core.Player.White;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TerminalViewTest {

    private final PieceBox box = new PieceBox(mock(UnderAttackRule.class));

    @Test
    void makeOutput() {
        final String output = new TerminalView().makeOutput(HashMap.of(
                A1, box.whitePawn,
                G7, box.blackRook,
                B7, box.blackBishop,
                E2, box.whiteQueen,
                F4, box.whiteKnight,
                B5, box.blackKing,
                D8, box.blackKnight),
                List.of(new Pawn(White), new Bishop(Black), new Knight(Black), new Rook(White), new Pawn(White)),
                List.of(F4, F4, F4, E2, E2).zip(List.of(E5, D6, C7, E1, F2)),
                List.of(G4, G4, G4, A2, A2).zip(List.of(D5, E3, C1, E3, F8)));

        assertThat(output).isEqualTo(
            "    A  B  C  D  E  F  G  H\n" +
            "   ╭──┬──┬──┬──┬──┬──┬──┬──╮\n" +
            " 8 │  │  │  │BN│  │  │  │  │ 8\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 7 │  │BB│  │  │  │  │BR│  │ 7\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 6 │  │  │  │  │  │  │  │  │ 6\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 5 │  │BK│  │  │  │  │  │  │ 5     Captured:\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤       WP WR WP\n" +
            " 4 │  │  │  │  │  │WN│  │  │ 4     BB BN\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 3 │  │  │  │  │  │  │  │  │ 3\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 2 │  │  │  │  │WQ│  │  │  │ 2\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 1 │WP│  │  │  │  │  │  │  │ 1\n" +
            "   ╰──┴──┴──┴──┴──┴──┴──┴──╯\n" +
            "    A  B  C  D  E  F  G  H\n" +
            "Black possible moves: F4-E5 F4-D6 F4-C7 E2-E1 E2-F2" +
            "White possible moves: G4-D5 G4-E3 G4-C1 A2-E3 A2-F8");
    }
}