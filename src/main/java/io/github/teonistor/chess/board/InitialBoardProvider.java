package io.github.teonistor.chess.board;

import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.PieceBox;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import lombok.AllArgsConstructor;
import lombok.NonNull;


@AllArgsConstructor
public class InitialBoardProvider {

    private final @NonNull PieceBox box;

    public Map<Position,Piece> createInitialBoard() {
        return HashMap.of(
                     Position.A1, box.whiteRook,
                     Position.B1, box.whiteKnight,
                     Position.C1, box.whiteBishop,
                     Position.D1, box.whiteQueen,
                     Position.E1, box.whiteKing,
                     Position.F1, box.whiteBishop,
                     Position.G1, box.whiteKnight,
                     Position.H1, box.whiteRook)

                .put(Position.A2, box.whitePawn)
                .put(Position.B2, box.whitePawn)
                .put(Position.C2, box.whitePawn)
                .put(Position.D2, box.whitePawn)
                .put(Position.E2, box.whitePawn)
                .put(Position.F2, box.whitePawn)
                .put(Position.G2, box.whitePawn)
                .put(Position.H2, box.whitePawn)

                .put(Position.A7, box.blackPawn)
                .put(Position.B7, box.blackPawn)
                .put(Position.C7, box.blackPawn)
                .put(Position.D7, box.blackPawn)
                .put(Position.E7, box.blackPawn)
                .put(Position.F7, box.blackPawn)
                .put(Position.G7, box.blackPawn)
                .put(Position.H7, box.blackPawn)

                .put(Position.A8, box.blackRook)
                .put(Position.B8, box.blackKnight)
                .put(Position.C8, box.blackBishop)
                .put(Position.D8, box.blackQueen)
                .put(Position.E8, box.blackKing)
                .put(Position.F8, box.blackBishop)
                .put(Position.G8, box.blackKnight)
                .put(Position.H8, box.blackRook);
    }
}
