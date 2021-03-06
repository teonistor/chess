package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.With;

import javax.annotation.Nullable;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class GameState {
    private final @NonNull Map<Position, Piece> board;
    private final @NonNull @With Player player;
    private final @NonNull List<Piece> capturedPieces;
    private final @Nullable GameState previous;

    public GameState advance(final Map<Position, Piece> newBoard) {
        return new GameState(newBoard, player.next(), capturedPieces, this);
    }

    public GameState advance(final Map<Position, Piece> newBoard, final Piece newCapturedPiece) {
        return new GameState(newBoard, player.next(), capturedPieces.append(newCapturedPiece), this);
    }
}
