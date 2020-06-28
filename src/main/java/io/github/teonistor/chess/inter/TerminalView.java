package io.github.teonistor.chess.inter;

import com.google.common.annotations.VisibleForTesting;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Bishop;
import io.github.teonistor.chess.piece.King;
import io.github.teonistor.chess.piece.Knight;
import io.github.teonistor.chess.piece.Pawn;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.Queen;
import io.github.teonistor.chess.piece.Rook;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;

import java.io.PrintStream;
import java.util.regex.Pattern;

public class TerminalView implements View {

    private static final String boardTemplate =
        "    A  B  C  D  E  F  G  H\n" +
        "   ╭──┬──┬──┬──┬──┬──┬──┬──╮\n" +
        " 8 │A8│B8│C8│D8│E8│F8│G8│H8│ 8\n" +
        "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
        " 7 │A7│B7│C7│D7│E7│F7│G7│H7│ 7\n" +
        "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
        " 6 │A6│B6│C6│D6│E6│F6│G6│H6│ 6\n" +
        "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
        " 5 │A5│B5│C5│D5│E5│F5│G5│H5│ 5\n" +
        "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
        " 4 │A4│B4│C4│D4│E4│F4│G4│H4│ 4\n" +
        "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
        " 3 │A3│B3│C3│D3│E3│F3│G3│H3│ 3\n" +
        "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
        " 2 │A2│B2│C2│D2│E2│F2│G2│H2│ 2\n" +
        "   ├──┼──┼──┼──┼──┼──┼──┼──┤\n" +
        " 1 │A1│B1│C1│D1│E1│F1│G1│H1│ 1\n" +
        "   ╰──┴──┴──┴──┴──┴──┴──┴──╯\n" +
        "    A  B  C  D  E  F  G  H";

    private static final Map<Class<? extends Piece>, String> pieceLetters = HashMap.of(Pawn.class, "P", Knight.class, "N", Rook.class, "R", Bishop.class, "B", Queen.class, "Q", King.class, "K");
    private static final Map<Player, String> playerLetters = HashMap.of(Player.Black, "B", Player.White, "W");
    private static final Pattern slot = Pattern.compile("[A-H][1-8]");

    private final PrintStream outStream;

    public TerminalView() {
        outStream = System.out;
    }


    @Override
    public void refresh(Map<Position, Piece> board, Player player, List<Piece> capturedPieces, Position source, Set<Position> targets) {
        outStream.print(makeOutput(board, player, source, targets));
    }

    @VisibleForTesting
    String makeOutput(Map<Position, Piece> board, Player player, Position source, Set<Position> targets) {
        // TODO Highlights, captured
        // Ugh, mutable
        String output = boardTemplate;
        for (Tuple2<Position,Piece> t : board) {
            output = output.replace(t._1.toString(), playerLetters.getOrElse(t._2.getPlayer(), " ") + pieceLetters.getOrElse(t._2.getClass(), " "));
        }
        output = slot.matcher(output).replaceAll("  ");
        output = String.format("%s%n%s moves.%n", output, player);
        if (source != Position.OutOfBoard) {
            output = String.format("%sSelected: %s%nPossible moves: %s%n", output, source, targets);
        }
        return output;
    }

    @Override
    public void announce(String message) {
        outStream.println(message);
    }
}

/*
  Keep this for posterity:

     A  B  C  D  E  F  G  H
    ╭──┬──┬──┬──┬──┬──┬──┬──╮
  8 │  │  │  │  │  │  │  │  │ 8
    ├──┼──┼──┼──┼──┼──┼──┼──┤
  7 │  │  │  │  │  │  │  │  │ 7
    ├──┼──┼──┼──┼──┼──┼──┼──┤
  6 │  │  │  │  │  │  │  │  │ 6
    ├──┼──┼──┼──┼──┼──┼──┼──┤
  5 │  │  │  │  │  │  │  │  │ 5
    ├──┼──┼──┼──┼──┼──┼──┼──┤
  4 │  │  │  │  │  │  │  │  │ 4
    ├──┼──┼──┼──┼──┼──┼──┼──┤
  3 │  │  │  │  │  │  │  │  │ 3
    ├──┼──┼──┼──┼──┼──┼──┼──┤
  2 │  │  │  │  │  │  │  │  │ 2
    ├──┼──┼──┼──┼──┼──┼──┼──┤
  1 │  │  │  │  │  │  │  │  │ 1
    ╰──┴──┴──┴──┴──┴──┴──┴──╯
     A  B  C  D  E  F  G  H

*/
