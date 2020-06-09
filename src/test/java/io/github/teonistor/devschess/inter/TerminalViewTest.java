package io.github.teonistor.devschess.inter;

import io.github.teonistor.devschess.piece.Knight;
import io.github.teonistor.devschess.piece.Pawn;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.teonistor.devschess.Player.Black;
import static io.github.teonistor.devschess.Player.White;
import static io.github.teonistor.devschess.board.Position.A1;
import static io.github.teonistor.devschess.board.Position.B3;
import static io.github.teonistor.devschess.board.Position.C3;
import static io.github.teonistor.devschess.board.Position.C5;
import static io.github.teonistor.devschess.board.Position.D2;
import static io.github.teonistor.devschess.board.Position.D4;
import static io.github.teonistor.devschess.board.Position.F6;
import static io.github.teonistor.devschess.board.Position.G7;
import static io.github.teonistor.devschess.board.Position.OutOfBoard;
import static org.assertj.core.api.Assertions.assertThat;

class TerminalViewTest {

    @Test
    void makeOutputWithoutSelection() {
        final String output = new TerminalView().makeOutput(HashMap.of(A1, new Pawn(White), G7, new Pawn(Black)), White, OutOfBoard, HashSet.empty());

        assertThat(output).contains(
            "    A  B  C  D  E  F  G  H\n" +
            "   ╭──┬──┬──┬──┬──┬──┬──┬──╮\n" +
            " 8 │  │  │  │  │  │  │  │  │ 8\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 7 │  │  │  │  │  │  │BP│  │ 7\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 6 │  │  │  │  │  │  │  │  │ 6\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 5 │  │  │  │  │  │  │  │  │ 5\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 4 │  │  │  │  │  │  │  │  │ 4\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 3 │  │  │  │  │  │  │  │  │ 3\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 2 │  │  │  │  │  │  │  │  │ 2\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 1 │WP│  │  │  │  │  │  │  │ 1\n" +
            "   ╰──┴──┴──┴──┴──┴──┴──┴──╯\n" +
            "    A  B  C  D  E  F  G  H",
            "White moves.");
    }

    @Test
    void makeOutputWithSelection() {
        final String output = new TerminalView().makeOutput(HashMap.of(B3, new Knight(Black), F6, new Pawn(White)), Black, C3, HashSet.of(C5, D4, D2));

        assertThat(output).contains(
            "    A  B  C  D  E  F  G  H\n" +
            "   ╭──┬──┬──┬──┬──┬──┬──┬──╮\n" +
            " 8 │  │  │  │  │  │  │  │  │ 8\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 7 │  │  │  │  │  │  │  │  │ 7\n" +
            "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
            " 6 │  │  │  │  │  │WP│  │  │ 6\n" +
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
