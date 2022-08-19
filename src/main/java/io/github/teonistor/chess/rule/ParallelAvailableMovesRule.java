package io.github.teonistor.chess.rule;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.GameStateKey;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.piece.King;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Tuple2;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import io.vavr.collection.Traversable;
import io.vavr.control.Option;

import static java.util.function.UnaryOperator.identity;


public class ParallelAvailableMovesRule extends AvailableMovesRule {

    public ParallelAvailableMovesRule(final CheckRule rule) {
        super(rule);
    }

    @Override
    public Map<GameStateKey, GameState> computeAvailableMoves(final GameState state) {
        return Option.some(state)

            .map(this::computeAvailableMovesAfterOneStep)
            .filter(this::noneEmpty)
            .map(this::computeAvailableMovesAfterAnotherStep)
            .getOrElse(Stream::empty)

            .groupBy(Tuple2::_1)
            .mapValues(this::atMostOneState)
            .filterValues(Option::isDefined)
            .mapValues(Option::get);
    }

    private Stream<Tuple2<GameStateKey, GameState>> computeAvailableMovesAfterAnotherStep(Stream<Traversable<Tuple2<GameStateKey, GameState>>> availableMovesAfterOneStepByPlayer) {
        return availableMovesAfterOneStepByPlayer
            .flatMap(identity())
            .flatMap(this::computeAvailableMovesTupled);
    }

    private boolean noneEmpty(Stream<? extends Traversable<?>> traversables) {
        return traversables.forAll(Traversable::nonEmpty);
    }

    private Stream<Traversable<Tuple2<GameStateKey, GameState>>> computeAvailableMovesAfterOneStep(GameState state) {
        return Stream.of(Player.values())
            .map(player -> new Tuple2<>(GameStateKey.NIL, state.withPlayer(player)))
            .map(this::computeAvailableMovesTupled);
    }

    private Map<GameStateKey, GameState> computeAvailableMovesTupled(Tuple2<GameStateKey, GameState> keyAndState) {
        return computeAvailableMoves(keyAndState._1, keyAndState._2);
    }

    private Option<GameState> atMostOneState(final Stream<Tuple2<GameStateKey, GameState>> states) {
        return states.map(Tuple2::_2)
             . reduceOption((a, b) -> equivalentStates(a, b) ? a : null)
             . flatMap(Option::of);
    }

    /**
     * @param a a game state
     * @param b another game state
     * @return true if by looking at boards set up in these states with your eyes you couldn't tell the difference (i.e. they are equivalent); false otherwise
     */
    private boolean equivalentStates(final GameState a, final GameState b) {
        return a.getBoard().equals(b.getBoard())
            && a.getCapturedPieces().equals(b.getCapturedPieces());
    }

    @Override
    protected boolean validateBoardwideRules(final Player player, final Map<Position, Piece> board) {
        // Protect CheckRule from a board with a missing king (which is possible when we look more than 1 step ahead)
        return board.exists(positionAndPiece -> isPlayersKing(positionAndPiece._2, player))
            && super.validateBoardwideRules(player, board);
    }

    private boolean isPlayersKing(final Piece piece, final Player player) {
        return piece instanceof King && piece.getPlayer() == player;
    }
}
