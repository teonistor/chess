package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.piece.Pawn;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EnPassant extends SingleOutcomeMove {

    private final @NonNull @Getter Position from;
    private final @NonNull @Getter Position to;
    private final @NonNull Position victimNow;
    private final @NonNull Position victimBefore;

    @Override
    public boolean validate(final GameState state) {
        final GameState previousState = state.getPrevious();
        if(previousState == null) {
            return false;
        }

        // Must check all 4: a relevant pawn must be here and not there and have been there and not have been here
        final Option<Piece> nowHere = state.getBoard().get(victimNow)
                .filter(victim -> victim.getClass().equals(Pawn.class))
                .filter(victim -> victim.getPlayer() != state.getPlayer());
        final Option<Piece> nowThere = state.getBoard().get(victimBefore);
        final Option<Piece> thenHere = previousState.getBoard().get(victimNow);
        final Option<Piece> thenThere = previousState.getBoard().get(victimBefore);

        return nowHere.isDefined() && nowThere.isEmpty()
            && thenHere.isEmpty() && thenThere.filter(nowHere.get()::equals).isDefined();
    }

    @Override
    protected GameState executeSingleOutcome(final GameState state, final Piece pieceToPlace) {
        return state.advance(state.getBoard().remove(from).remove(victimNow).put(to, pieceToPlace), state.getBoard().get(victimNow).get());
    }
}
