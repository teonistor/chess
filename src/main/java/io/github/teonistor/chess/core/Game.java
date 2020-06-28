package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.move.Move;
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

        while (!gameOverChecker.isOver(state.getBoard(), state.getPlayer())) {
            state = playOne(state);
            view.refresh(state.getBoard(), state.getPlayer(), state.getCapturedPieces(), Position.OutOfBoard, HashSet.empty());
        }
    }

    private GameState playOne(GameState state) {
        final Map<Position, Piece> board = state.getBoard();
        final Player player = state.getPlayer();

        final Map<Position, Map<Position, Move>> plausibleMoves = board.filterValues(piece -> piece.getPlayer() == player)
                .map((from, piece) -> new Tuple2<>(from, piece.computePossibleMoves(from)
                .filter(move -> move.validate(board))
                .filter(move -> checkRule.validateMove(board, player, move))
                .collect(HashMap.collector(Move::getTo))));

        return takeFirstInput(state, plausibleMoves);
    }

    private GameState takeFirstInput(GameState state, Map<Position, Map<Position, Move>> possibleMoves) {
        return inputs[state.getPlayer().ordinal()].takeInput(source -> {

            if (possibleMoves.containsKey(source)) {
                return takeSecondInput(state, possibleMoves, source, possibleMoves.get(source).get());
            } else {
                if(source != Position.OutOfBoard) {
                    view.announce("Invalid pickup: " + source);
                }
                return takeFirstInput(state, possibleMoves);
            }

        }, (source, target) -> {
            final Option<Move> move = possibleMoves.get(source).flatMap(m -> m.get(target));
            if (move.isDefined()) {
                view.announce(String.format("%s moves: %s - %s", state.getPlayer(), source, target));
                return move.get().execute(state.getBoard(), state::advance, state::advance);
            } else {
                // here we short-circuit to second input in 2 step input
                if(source != Position.OutOfBoard && target != Position.OutOfBoard) {
                    view.announce(String.format("Invalid move: %s - %s", source, target));
                }
                return takeFirstInput(state, possibleMoves);
            }
        });
    }

    private GameState takeSecondInput(GameState state, Map<Position, Map<Position, Move>> possibleMoves, Position source, Map<Position, Move> filteredMoves) {
        return inputs[state.getPlayer().ordinal()].takeInput(target -> {
            if (filteredMoves.containsKey(target)) {
                view.announce(String.format("%s moves: %s - %s", state.getPlayer(), source, target));
                return filteredMoves.get(target).get().execute(state.getBoard(), state::advance, state::advance);

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
        });
    }
}
