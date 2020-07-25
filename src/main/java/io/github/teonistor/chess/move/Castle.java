package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.core.UnderAttackRule;
import io.github.teonistor.chess.piece.King;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.Rook;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import static io.github.teonistor.chess.board.Position.A1;
import static io.github.teonistor.chess.board.Position.A8;
import static io.github.teonistor.chess.board.Position.B1;
import static io.github.teonistor.chess.board.Position.B8;
import static io.github.teonistor.chess.board.Position.C1;
import static io.github.teonistor.chess.board.Position.C8;
import static io.github.teonistor.chess.board.Position.D1;
import static io.github.teonistor.chess.board.Position.D8;
import static io.github.teonistor.chess.board.Position.E1;
import static io.github.teonistor.chess.board.Position.E8;
import static io.github.teonistor.chess.board.Position.F1;
import static io.github.teonistor.chess.board.Position.F8;
import static io.github.teonistor.chess.board.Position.G1;
import static io.github.teonistor.chess.board.Position.G8;
import static io.github.teonistor.chess.board.Position.H1;
import static io.github.teonistor.chess.board.Position.H8;


@AllArgsConstructor
public class Castle implements Move {
    // TODO Once we implement global check prevention some of these will be redundant
    public static final HashMap<Position,HashSet<Position>> mustNotBeUnderAttackByTarget = HashMap.of(
            G1, HashSet.of(E1, F1, G1), C1, HashSet.of(C1, D1, E1),
            G8, HashSet.of(E8, F8, G8), C8, HashSet.of(C8, D8, E8));
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
        Map<Position, Piece> board = state.getBoard();
        Player player = state.getPlayer();
        return mustBeEmptyByTarget.get(to).get().toStream().map(board::get).filter(Option::isDefined).isEmpty()
            // TODO Insufficient: The rook must have not moved either
            && rookPositionsByTarget.get(to).flatMap(board::get).filter(piece -> piece.getPlayer() == player && piece.getClass().equals(Rook.class)).isDefined()
            && mustNotBeUnderAttackByTarget.get(to).get().toStream().filter(position -> underAttackRule.checkAttack(board, position, player)).isEmpty();
    }

    @Override
    public GameState execute(GameState state) {
        final Map<Position, Piece> board = state.getBoard();
        final Position rookFrom = rookPositionsByTarget.get(to).get();
        final Position rookTo = rookTargetsByTarget.get(to).get();
        final Piece oldKing = board.get(from).get();

        return state.advance(board.remove(from).remove(rookFrom)
              .put(to, new King(oldKing.getPlayer()))
              .put(rookTo, board.get(rookFrom).get()));
    }
}
