package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
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

import java.util.function.BiFunction;
import java.util.function.Function;

import static io.github.teonistor.chess.board.Position.*;


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
    private final @NonNull Player player;
    private final @NonNull UnderAttackRule underAttackRule;
    
    @Override
    public boolean validate(Map<Position, Piece> board) {
        return mustBeEmptyByTarget.get(to).get().toStream().map(board::get).filter(Option::isDefined).isEmpty()
            // TODO Insufficient: The rook must have not moved either
            && rookPositionsByTarget.get(to).flatMap(board::get).filter(piece -> piece.getPlayer() == player && piece.getClass().equals(Rook.class)).isDefined()
            && mustNotBeUnderAttackByTarget.get(to).get().toStream().filter(position -> underAttackRule.checkAttack(board, position, player)).isEmpty();
    }

    @Override
    public <T> T execute(Map<Position, Piece> board, Function<Map<Position, Piece>, T> nonCapturingCallback, BiFunction<Map<Position, Piece>, Piece, T> capturingCallback) {
        final Position rookFrom = rookPositionsByTarget.get(to).get();
        final Position rookTo = rookTargetsByTarget.get(to).get();

        return nonCapturingCallback.apply(board.remove(from).remove(rookFrom)
              .put(to, new King(player))
              .put(rookTo, board.get(rookFrom).get()));
    }
}
