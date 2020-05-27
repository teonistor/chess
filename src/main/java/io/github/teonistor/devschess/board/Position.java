package io.github.teonistor.devschess.board;

import java.util.Optional;
import java.util.function.Predicate;
import static java.util.stream.IntStream.range;


public enum Position {

    OutOfBoard,
    A8, B8, C8, D8, E8, F8, G8, H8,
    A7, B7, C7, D7, E7, F7, G7, H7,
    A6, B6, C6, D6, E6, F6, G6, H6,
    A5, B5, C5, D5, E5, F5, G5, H5,
    A4, B4, C4, D4, E4, F4, G4, H4,
    A3, B3, C3, D3, E3, F3, G3, H3,
    A2, B2, C2, D2, E2, F2, G2, H2,
    A1, B1, C1, D1, E1, F1, G1, H1;

    private static final Position[][] board = new Position[][]{
            {A8, B8, C8, D8, E8, F8, G8, H8},
            {A7, B7, C7, D7, E7, F7, G7, H7},
            {A6, B6, C6, D6, E6, F6, G6, H6},
            {A5, B5, C5, D5, E5, F5, G5, H5},
            {A4, B4, C4, D4, E4, F4, G4, H4},
            {A3, B3, C3, D3, E3, F3, G3, H3},
            {A2, B2, C2, D2, E2, F2, G2, H2},
            {A1, B1, C1, D1, E1, F1, G1, H1}};

    static {
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
            System.out.print("{A");
            System.out.print(8 - i);
            range(1,8).forEach(j -> {
                System.out.print(", ");
                System.out.print((char)('A' + j));
                System.out.print(8 - i);
            });
            System.out.println("},");
        });
        System.out.println();
        range(0,8).forEach(i -> {
            range(0,8).forEach(j -> {
                System.out.print((char)('A' + j));
                System.out.print(8 - i);
                System.out.print(", ");
            });
            System.out.println();
        });
    }
}
