package io.github.teonistor.devschess;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static io.github.teonistor.devschess.Board.Position.*;
import static java.util.stream.IntStream.range;


public class Board {

    static {
        final Position[][] board = new Position[][]{
            {H1, H2, H3, H4, H5, H6, H7, H8},
            {G1, G2, G3, G4, G5, G6, G7, G8},
            {F1, F2, F3, F4, F5, F6, F7, F8},
            {E1, E2, E3, E4, E5, E6, E7, E8},
            {D1, D2, D3, D4, D5, D6, D7, D8},
            {C1, C2, C3, C4, C5, C6, C7, C8},
            {B1, B2, B3, B4, B5, B6, B7, B8},
            {A1, A2, A3, A4, A5, A6, A7, A8}};

        final Predicate<Integer> safe = x -> x >= 0 && x < 8;

        range(0,8).forEach(i -> range(0,8).forEach(j -> {
            board[i][j].up    = Optional.of(i-1).filter(safe).map(s -> board[s][j]).orElse(OutOfBoard);
            board[i][j].down  = Optional.of(i+1).filter(safe).map(s -> board[s][j]).orElse(OutOfBoard);
            board[i][j].left  = Optional.of(j-1).filter(safe).map(s -> board[i][s]).orElse(OutOfBoard);
            board[i][j].right = Optional.of(j+1).filter(safe).map(s -> board[i][s]).orElse(OutOfBoard);
        }));

        OutOfBoard.up    = OutOfBoard;
        OutOfBoard.down  = OutOfBoard;
        OutOfBoard.left  = OutOfBoard;
        OutOfBoard.right = OutOfBoard;
    }

    public static List<Object> initialSetup() {
        return ImmutableList.of(

        );
    }


    public enum Position {

        OutOfBoard,
        H1, H2, H3, H4, H5, H6, H7, H8,
        G1, G2, G3, G4, G5, G6, G7, G8,
        F1, F2, F3, F4, F5, F6, F7, F8,
        E1, E2, E3, E4, E5, E6, E7, E8,
        D1, D2, D3, D4, D5, D6, D7, D8,
        C1, C2, C3, C4, C5, C6, C7, C8,
        B1, B2, B3, B4, B5, B6, B7, B8,
        A1, A2, A3, A4, A5, A6, A7, A8;

        // These would love to be final but enum constants can't be referenced in the constructors of other constants of the same enum
        private Position left, right, up, down;

        public Position left() {
            return left;
        }

        public Position right() {
            return right;
        }

        public Position up() {
            return up;
        }

        public Position down() {
            return down;
        }


        // Code-generating code
        public static void main(String[] args) {
            range(0,8).forEach(i -> {
                System.out.print("{");
                char c = (char) ('H' - i);
                range(1,9).forEach(j -> {
                    System.out.print(", Position.");
                    System.out.print(c);
                    System.out.print(j);
                });
                System.out.println("},");
            });
            System.out.println();
            range(0,8).forEach(i -> {
                char c = (char) ('H' - i);
                range(1,9).forEach(j -> {
                    System.out.print(c);
                    System.out.print(j);
                    System.out.print(", ");
                });
                System.out.println();
            });
        }
    }
}
