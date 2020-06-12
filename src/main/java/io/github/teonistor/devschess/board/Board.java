package io.github.teonistor.devschess.board;

import io.github.teonistor.devschess.piece.Bishop;
import io.github.teonistor.devschess.piece.Knight;
import io.github.teonistor.devschess.piece.Pawn;
import io.github.teonistor.devschess.piece.Piece;
import io.github.teonistor.devschess.piece.Queen;
import io.github.teonistor.devschess.piece.Rook;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

import static io.github.teonistor.devschess.Player.Black;
import static io.github.teonistor.devschess.Player.White;


public class Board {

    public static Map<Position, Piece> initialSetup() {
        // TODO Do when we have all pieces
        return HashMap.of(
                     Position.A1, new Rook(White),
                     Position.B1, new Knight(White),
                     Position.C1, new Bishop(White),
                     Position.D1, new Queen(White),
                     Position.F1, new Bishop(White),
                     Position.G1, new Knight(White),
                     Position.H1, new Rook(White))
                .put(Position.A2, new Pawn(White))
                .put(Position.B2, new Pawn(White))
                .put(Position.C2, new Pawn(White))
                .put(Position.D2, new Pawn(White))
                .put(Position.E2, new Pawn(White))
                .put(Position.F2, new Pawn(White))
                .put(Position.G2, new Pawn(White))
                .put(Position.H2, new Pawn(White))

                .put(Position.A7, new Pawn(Black))
                .put(Position.B7, new Pawn(Black))
                .put(Position.C7, new Pawn(Black))
                .put(Position.D7, new Pawn(Black))
                .put(Position.E7, new Pawn(Black))
                .put(Position.F7, new Pawn(Black))
                .put(Position.G7, new Pawn(Black))
                .put(Position.H7, new Pawn(Black))

                .put(Position.A8, new Rook(Black))
                .put(Position.B8, new Knight(Black))
                .put(Position.C8, new Bishop(Black))
                .put(Position.D8, new Queen(Black))
                .put(Position.F8, new Bishop(Black))
                .put(Position.G8, new Knight(Black))
                .put(Position.H8, new Rook(Black));
    }
}
