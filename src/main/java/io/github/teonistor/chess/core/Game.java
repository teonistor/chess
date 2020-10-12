package io.github.teonistor.chess.core;

import com.google.common.annotations.VisibleForTesting;
import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import lombok.Getter;

public class Game {

    private final GameStateProvider gameStateProvider;
    private final CheckRule checkRule;
    private final GameOverChecker gameOverChecker;
    private final Input[] inputs;
    private final View view;

    // Mutable state!
    private @Getter GameState state;
    private Map<Position, Map<Position, GameState>> possibleMoves;
    private @Getter GameCondition condition;

    public Game(final GameStateProvider gameStateProvider, final CheckRule checkRule, final GameOverChecker gameOverChecker, final Input white, final Input black, final View view) {
        this.gameStateProvider = gameStateProvider;
        this.checkRule = checkRule;
        this.gameOverChecker = gameOverChecker;
        inputs = new Input[2];
        inputs[Player.White.ordinal()] = white;
        inputs[Player.Black.ordinal()] = black;
        this.view = view;

        this.state = gameStateProvider.createState();
        recomputeMovesAndCondition();
        view.refresh(state.getBoard(), state.getPlayer(), state.getCapturedPieces(), turnMovesIntoPairs(possibleMoves));
    }

    @Deprecated
    public void play() {

    }

    public void playRound(final Position source, final Position target, final View view) {
        executeMove(source, target);
        recomputeMovesAndCondition();
        view.refresh(state.getBoard(), state.getPlayer(), state.getCapturedPieces(), turnMovesIntoPairs(possibleMoves));

        switch (condition) {
            case Continue:
                break;

            case WhiteWins:
                view.announce("White wins!");
                break;

            case BlackWins:
                view.announce("Black wins!");
                break;

            case Stalemate:
                view.announce("Stalemate!");
                break;

            default:
                throw new IllegalStateException("Unexpected value for GameCondition: " + condition);
        }
    }

    private void recomputeMovesAndCondition() {
        possibleMoves = computeAvailableMoves(state);
        condition = gameOverChecker.check(state.getBoard(), state.getPlayer(), possibleMoves);
    }

    @VisibleForTesting
    Map<Position, Map<Position, GameState>> computeAvailableMoves(final GameState state) {
        final Map<Position,Piece> board = state.getBoard();
        final Player player = state.getPlayer();

        return board.filterValues(piece -> piece.getPlayer() == player)
                // .mapValues ?
                .map((from, piece) -> new Tuple2<>(from, piece.computePossibleMoves(from)
                .filter(move -> move.validate(state))
                .map(move -> new Tuple2<>(move.getTo(), move.execute(state)))
                .filter(targetAndState -> checkRule.validate(targetAndState._2.getBoard(), player))
                .collect(HashMap.collector())));
    }

    @VisibleForTesting
    GameState takeFirstInput(final GameState state, final Map<Position, Map<Position,GameState>> possibleMoves) {
        return inputs[state.getPlayer().ordinal()].takeInput(
                source -> processFirstInput(state, possibleMoves, source),
                (source, target) -> processFirstAndSecondInput(state, possibleMoves, source, target));
    }

    private GameState takeSecondInput(final GameState state, final Map<Position, Map<Position,GameState>> possibleMoves, final Position source, final Map<Position,GameState> filteredMoves) {
        return inputs[state.getPlayer().ordinal()].takeInput(target -> processSecondInput(state, possibleMoves, source, filteredMoves, target));
    }

    private GameState processFirstInput(final GameState state, final Map<Position, Map<Position, GameState>> possibleTargetStates, final Position source) {
        if (possibleTargetStates.containsKey(source)) {
            // TODO What's with this print here
            System.out.println(possibleTargetStates);
            final Map<Position, GameState> filteredTargetStates = possibleTargetStates.get(source).get();
            view.refresh(state.getBoard(), state.getPlayer(), state.getCapturedPieces(), turnMovesIntoPairs(possibleMoves));
            return takeSecondInput(state, possibleTargetStates, source, filteredTargetStates);
        } else {
            if(source != Position.OutOfBoard) {
                view.announce("Invalid pickup: " + source);
            }
            return takeFirstInput(state, possibleTargetStates);
        }
    }

    private GameState processFirstAndSecondInput(final GameState state, final Map<Position, Map<Position, GameState>> possibleMoves, final Position source, final Position target) {
        final Option<GameState> move = possibleMoves.get(source).flatMap(m -> m.get(target));
        if (move.isDefined()) {
            view.announce(String.format("%s moves: %s - %s", state.getPlayer(), source, target));
            return move.get();
        } else {
            if(source != Position.OutOfBoard && target != Position.OutOfBoard) {
                view.announce(String.format("Invalid move: %s - %s", source, target));
            }
            return takeFirstInput(state, possibleMoves);
        }
    }

    private GameState processSecondInput(final GameState state, final Map<Position, Map<Position, GameState>> possibleMoves, final Position source, final Map<Position, GameState> filteredMoves, final Position target) {
        if (filteredMoves.containsKey(target)) {
            view.announce(String.format("%s moves: %s - %s", state.getPlayer(), source, target));
            return filteredMoves.get(target).get();

        } else {
            if(target == Position.OutOfBoard) {
                // You can put a piece back down in computer chess because perhaps the input is bogus
                view.announce("Cancel.");
                return takeFirstInput(state, possibleMoves);
            } else {
                view.announce(String.format("Invalid move: %s - %s", source, target));
                return takeSecondInput(state, possibleMoves, source, filteredMoves);
            }
        }
    }

    private Stream<Tuple2<Position, Position>> turnMovesIntoPairs(Map<Position, Map<Position, GameState>> possibleMoves) {
        return possibleMoves.toStream().flatMap(m -> Stream.continually(m._1).zip(m._2.keySet()));
    }

    private void executeMove(final Position source, final Position target) {
        final Option<GameState> move = possibleMoves.get(source).flatMap(m -> m.get(target));
        if (move.isDefined()) {
            view.announce(String.format("%s moves: %s - %s", state.getPlayer(), source, target));
            state = move.get();
        } else {
            view.announce(String.format("Invalid move: %s - %s", source, target));
        }
    }
}
