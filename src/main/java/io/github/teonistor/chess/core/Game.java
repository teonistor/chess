package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.move.Move;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.control.Option;

public class Game {

    private final InitialStateProvider initialStateProvider;
    private final UnderAttackRule underAttackRule;
    private final GameOverChecker gameOverChecker;
    private final Input[] inputs;
    private final View view;

    public Game(InitialStateProvider initialStateProvider, UnderAttackRule underAttackRule, GameOverChecker gameOverChecker, Input white, Input black, View view) {
        this.initialStateProvider = initialStateProvider;
        this.underAttackRule = underAttackRule;
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
            state = takeFirstInput(state);
            view.refresh(state.getBoard(), state.getPlayer(), state.getCapturedPieces(), Position.OutOfBoard, HashSet.empty());
        }
    }

    private GameState takeFirstInput(GameState state) {
        final Position source = inputs[state.getPlayer().ordinal()].takeInput();
        final Option<Piece> sourcePiece = state.getBoard().get(source).filter(p -> state.getPlayer().equals(p.getPlayer()));

        if (sourcePiece.isDefined()) {
            final Map<Position,Move> moves = sourcePiece.get().computePossibleMoves(source)
                    .filter(move -> move.validate(state.getBoard()))
                    .collect(HashMap.collector(Move::getTo));
            view.refresh(state.getBoard(), state.getPlayer(), state.getCapturedPieces(), source, moves.keySet());

            return takeSecondInput(state, source, moves);
        }

        if(source != Position.OutOfBoard) {
            view.announce("Invalid pickup: " + source);
        }
        return takeFirstInput(state);
    }

    private GameState takeSecondInput(GameState state, Position source, Map<Position, Move> moves) {
        final Position target = inputs[state.getPlayer().ordinal()].takeInput();

        if (moves.containsKey(target)) {
            view.announce(String.format("%s moves: %s - %s", state.getPlayer(), source, target));

            return moves.get(target).get().execute(state.getBoard(), state::advance, state::advance);
        }

        if(target == Position.OutOfBoard) {
            // You can put a piece back down in computer chess because perhaps the input is bogus
            view.announce("Cancel.");
            return takeFirstInput(state);
        }
        view.announce(String.format("Invalid move: %s - %s", source, target));
        return takeSecondInput(state, source, moves);
    }
}
