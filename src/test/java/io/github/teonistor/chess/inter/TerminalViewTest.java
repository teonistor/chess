package io.github.teonistor.chess.inter;

import io.github.teonistor.chess.piece.Bishop;
import io.github.teonistor.chess.piece.King;
import io.github.teonistor.chess.piece.Knight;
import io.github.teonistor.chess.piece.Pawn;
import io.github.teonistor.chess.piece.Queen;
import io.github.teonistor.chess.piece.Rook;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.teonistor.chess.core.Player.Black;
import static io.github.teonistor.chess.core.Player.White;
import static io.github.teonistor.chess.board.Position.*;
import static org.assertj.core.api.Assertions.assertThat;

class TerminalViewTest {

    @Test
    void makeOutputWithoutSelection() {
        final String output = new TerminalView().makeOutput(HashMap.of(
                A1, new Pawn(White),
                G7, new Rook(Black),
                B7, new Bishop(Black),
                E2, new Queen(White),
                F4, new Knight(White),
                B5, new King(Black),
                D8, new Knight(Black)), White, OutOfBoard, HashSet.empty());

        assertThat(output).contains(
            "    A  B  C  D  E  F  G  H\n" +
            "   ╭──┬──┬──┬──┬──┬──┬──┬──╮\n" +
            " 8 │  │  │  │BN│  │  │  │  │ 8\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 7 │  │BB│  │  │  │  │BR│  │ 7\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 6 │  │  │  │  │  │  │  │  │ 6\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 5 │  │BK│  │  │  │  │  │  │ 5\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 4 │  │  │  │  │  │WN│  │  │ 4\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 3 │  │  │  │  │  │  │  │  │ 3\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 2 │  │  │  │  │WQ│  │  │  │ 2\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 1 │WP│  │  │  │  │  │  │  │ 1\n" +
            "   ╰──┴──┴──┴──┴──┴──┴──┴──╯\n" +
            "    A  B  C  D  E  F  G  H",
            "White moves.");
    }

    @Test
    void makeOutputWithSelection() {
        final String output = new TerminalView().makeOutput(HashMap.of(
                B3, new Knight(Black),
                F6, new Pawn(White),
                C6, new Rook(White),
                E8, new King(Black)), Black, C3, HashSet.of(C5, D4, D2));

        assertThat(output).contains(
            "    A  B  C  D  E  F  G  H\n" +
            "   ╭──┬──┬──┬──┬──┬──┬──┬──╮\n" +
            " 8 │  │  │  │  │BK│  │  │  │ 8\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 7 │  │  │  │  │  │  │  │  │ 7\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 6 │  │  │WR│  │  │WP│  │  │ 6\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 5 │  │  │  │  │  │  │  │  │ 5\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 4 │  │  │  │  │  │  │  │  │ 4\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 3 │  │BN│  │  │  │  │  │  │ 3\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 2 │  │  │  │  │  │  │  │  │ 2\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 1 │  │  │  │  │  │  │  │  │ 1\n" +
            "   ╰──┴──┴──┴──┴──┴──┴──┴──╯\n" +
            "    A  B  C  D  E  F  G  H",
            "Black moves.",
            "Selected: C3");
        final Matcher match = Pattern.compile("Possible moves: (.+)").matcher(output);
        assertThat(match.find()).isTrue();
        assertThat(match.group(1)).contains("C5", "D4", "D2");
    }
}
