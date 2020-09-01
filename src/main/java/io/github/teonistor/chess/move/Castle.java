package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.core.UnderAttackRule;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.Rook;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import static io.github.teonistor.chess.board.Position.*;


@AllArgsConstructor
public class Castle implements Move {
    public static final HashMap<Position,HashSet<Position>> mustNotBeUnderAttackByTarget = HashMap.of(
            G1, HashSet.of(E1, F1), C1, HashSet.of(D1, E1),
            G8, HashSet.of(E8, F8), C8, HashSet.of(D8, E8));
    public static final HashMap<Position,HashSet<Position>> mustBeEmptyByTarget = HashMap.of(
            G1, HashSet.of(F1, G1), C1, HashSet.of(B1, C1, D1),
            G8, HashSet.of(F8, G8), C8, HashSet.of(B8, C8, D8));
    public static final HashMap<Position,Position> rookPositionsByTarget = HashMap.of(
            G1, H1, C1, A1,
            G8, H8, C8, A8);
    public static final HashMap<Position,Position> rookTargetsByTarget = HashMap.of(
            G1, F1, C1, D1,
            G8, F8, C8, D8);

    private final @NonNull @Getter Position from;
    private final @NonNull @Getter Position to;
    private final @NonNull UnderAttackRule underAttackRule;

    @Override
    public boolean validate(GameState state) {
        final Map<Position, Piece> board = state.getBoard();
        final Player player = state.getPlayer();

        final Option<Piece> possiblyRook = rookPositionsByTarget.get(to).flatMap(board::get);
        final Option<Piece> hopefullyKing = board.get(from);

        return mustBeEmptyByTarget.get(to).get().toStream().map(board::get).filter(Option::isDefined).isEmpty()
            && possiblyRook.filter(new Rook(player)::equals).isDefined()
            && mustNotBeUnderAttackByTarget.get(to).get().toStream().filter(position -> underAttackRule.checkAttack(board, position, player)).isEmpty()
            && neverMoved(state.getPrevious(), rookPositionsByTarget.get(to).get(), possiblyRook.get())
            && neverMoved(state.getPrevious(), from, hopefullyKing.get());
    }

    @Override
    public GameState execute(GameState state) {
        final Map<Position, Piece> board = state.getBoard();
        final Position rookFrom = rookPositionsByTarget.get(to).get();
        final Position rookTo = rookTargetsByTarget.get(to).get();

        return state.advance(board.remove(from).remove(rookFrom)
              .put(to, board.get(from).get())
              .put(rookTo, board.get(rookFrom).get()));
    }

    private boolean neverMoved(GameState state, Position position, Piece piece) {
        if (state == null) {
            return true;

        } else {
            return state.getBoard().get(position).filter(piece::equals).isDefined() && neverMoved(state.getPrevious(), position, piece);
        }
    }
}
