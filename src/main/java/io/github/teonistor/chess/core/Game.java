package io.github.teonistor.chess.core;

import com.google.common.annotations.VisibleForTesting;
import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.control.Option;

public class Game {

    private final InitialStateProvider initialStateProvider;
    private final CheckRule checkRule;
    private final GameOverChecker gameOverChecker;
    private final Input[] inputs;
    private final View view;

    public Game(InitialStateProvider initialStateProvider, CheckRule checkRule, GameOverChecker gameOverChecker, Input white, Input black, View view) {
        this.initialStateProvider = initialStateProvider;
        this.checkRule = checkRule;
        this.gameOverChecker = gameOverChecker;
        inputs = new Input[2];
        inputs[Player.White.ordinal()] = white;
        inputs[Player.Black.ordinal()] = black;
        this.view = view;
    }

    public void play() {
        GameState state = initialStateProvider.createInitialState();
        view.refresh(state.getBoard(), state.getPlayer(), state.getCapturedPieces(), Position.OutOfBoard, HashSet.empty());

        while (true) {
            final Map<Position, Map<Position,GameState>> possibleMoves = computeAvailableMoves(state);
            final GameCondition gameCondition = gameOverChecker.check(state.getBoard(), state.getPlayer(), possibleMoves);

            switch (gameCondition) {
                case Continue:
                    state = takeFirstInput(state, possibleMoves);
                    view.refresh(state.getBoard(), state.getPlayer(), state.getCapturedPieces(), Position.OutOfBoard, HashSet.empty());
                    continue;

                case WhiteWins:
                    view.announce("White wins!");
                    break;
                case BlackWins:
                    view.announce("Black wins!");
                    break;
                case Stalemate:
                    view.announce("Stalemate!");
                    break;
            }
            break;
        }
    }

    @VisibleForTesting
    Map<Position, Map<Position, GameState>> computeAvailableMoves(GameState state) {
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
    GameState takeFirstInput(GameState state, Map<Position, Map<Position,GameState>> possibleMoves) {
        return inputs[state.getPlayer().ordinal()].takeInput(
                source -> processFirstInput(state, possibleMoves, source),
                (source, target) -> processFirstAndSecondInput(state, possibleMoves, source, target));
    }

    private GameState takeSecondInput(GameState state, Map<Position, Map<Position,GameState>> possibleMoves, Position source, Map<Position,GameState> filteredMoves) {
        return inputs[state.getPlayer().ordinal()].takeInput(target -> processSecondInput(state, possibleMoves, source, filteredMoves, target));
    }

    private GameState processFirstInput(GameState state, Map<Position, Map<Position, GameState>> possibleTargetStates, Position source) {
        if (possibleTargetStates.containsKey(source)) {
            // TODO What's with this print here
            System.out.println(possibleTargetStates);
            final Map<Position, GameState> filteredTargetStates = possibleTargetStates.get(source).get();
            view.refresh(state.getBoard(), state.getPlayer(), state.getCapturedPieces(), source, filteredTargetStates.keySet());
            return takeSecondInput(state, possibleTargetStates, source, filteredTargetStates);
        } else {
            if(source != Position.OutOfBoard) {
                view.announce("Invalid pickup: " + source);
            }
            return takeFirstInput(state, possibleTargetStates);
        }
    }

    private GameState processFirstAndSecondInput(GameState state, Map<Position, Map<Position, GameState>> possibleMoves, Position source, Position target) {
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

    private GameState processSecondInput(GameState state, Map<Position, Map<Position, GameState>> possibleMoves, Position source, Map<Position, GameState> filteredMoves, Position target) {
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
}
