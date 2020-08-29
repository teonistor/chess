package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;

@AllArgsConstructor
@Getter
public class GameState {
    private final Map<Position, Piece> board;
    private final Player player;
    private final Set<Piece> capturedPieces;
    private final @Nullable GameState previous;
    private final @Nullable Position pawnTrail;

    public GameState(Map<Position, Piece> board, Player player, Set<Piece> capturedPieces, GameState previous) {
        this(board, player, capturedPieces, previous, null);
    }


    public GameState advance(Map<Position, Piece> newBoard) {
        return new GameState(newBoard, player.next(), capturedPieces, this);
    }

    public GameState advance(Map<Position, Piece> newBoard, Position pawnTrail) {
        return new GameState(newBoard, player.next(), capturedPieces, this, pawnTrail);
    }

    public GameState advance(Map<Position, Piece> newBoard, Piece newCapturedPiece) {
        return new GameState(newBoard, player.next(), capturedPieces.add(newCapturedPiece), this);
    }
}
