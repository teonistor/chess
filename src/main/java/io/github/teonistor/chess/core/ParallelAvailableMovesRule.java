package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.King;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Tuple2;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import io.vavr.control.Option;


public class ParallelAvailableMovesRule extends AvailableMovesRule {

    public ParallelAvailableMovesRule(final CheckRule rule) {
        super(rule);
    }

    @Override
    public Map<GameStateKey, GameState> computeAvailableMoves(final GameState state) {
        return Stream.of(Player.values())
             . map(player -> new Tuple2<>(GameStateKey.NIL, state.withPlayer(player)))

             . flatMap(this::computeAvailableMoves)
             . flatMap(this::computeAvailableMoves)

             . groupBy(Tuple2::_1)
             . mapValues(this::atMostOneState)
             . filterValues(Option::isDefined)
             . mapValues(Option::get);
    }

    public Map<GameStateKey, GameState> computeAvailableMoves(final Tuple2<GameStateKey, GameState> keyAndState) {
        return computeAvailableMoves(keyAndState._1, keyAndState._2);
    }

    private Option<GameState> atMostOneState(final Stream<Tuple2<GameStateKey, GameState>> states) {
        return states.map(Tuple2::_2)
             . reduceOption((a, b) -> equivalentStates(a, b) ? a : null)
             . flatMap(Option::of);
    }

    private boolean equivalentStates(final GameState a, final GameState b) {
        return a.getBoard().equals(b.getBoard())
            && a.getCapturedPieces().equals(b.getCapturedPieces());
    }

    @Override
    protected boolean validateBoardwideRules(Player player, Map<Position, Piece> board) {
        return board.exists(positionAndPiece -> {
            final Piece piece = positionAndPiece._2;
            return isPlayersKing(piece, player);
        })
            && super.validateBoardwideRules(player, board);
    }

    private boolean isPlayersKing(Piece piece, Player player) {
        return piece instanceof King && piece.getPlayer() == player;
    }
}
