package io.github.teonistor.devschess;

import io.github.teonistor.devschess.board.Position;
import io.github.teonistor.devschess.piece.Piece;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class GameState {
    private final Map<Position, Piece> board;
    private final Player player;
    private final Set<Piece> capturedPieces;

    public GameState advance(Map<Position, Piece> board) {
        return new GameState(board, player.next(), capturedPieces);
    }

    public GameState advance(Map<Position, Piece> newBoard, Piece newCapturedPiece) {
        return new GameState(newBoard, player.next(), capturedPieces.add(newCapturedPiece));
    }
}
