package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.piece.Piece;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EnPassant implements Move {

    private final @NonNull @Getter Position from;
    private final @NonNull @Getter Position to;
    private final @NonNull Position victim;

    @Override
    public boolean validate(GameState state) {
        // Heavily relying on the two-square pawn move postcondition
        return to == state.getPawnTrail();
    }

    @Override
    public GameState execute(GameState state) {
        final Piece piece = state.getBoard().get(from).get();
        return state.advance(state.getBoard().remove(from).remove(victim).put(to, piece), state.getBoard().get(victim).get());
    }
}
