package io.github.teonistor.chess.core;

import com.google.common.annotations.VisibleForTesting;
import io.github.teonistor.chess.board.Board;
import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.move.Move;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.control.Option;

import static io.github.teonistor.chess.core.Player.White;

public class Game {

    private final Input[] inputs;
    private final View views;

    public Game(Input white, Input black, View...views) {
        inputs = new Input[2];
        inputs[Player.White.ordinal()] = white;
        inputs[Player.Black.ordinal()] = black;

        // TODO This view stuff is untested and doesn't really belong here
        switch (views.length) {
            case 0:
                throw new IllegalArgumentException("Dude wtf");
            case 1:
                this.views = views[0];
                break;
            default:
                this.views = new View() {
                    private Iterable<View> vi = List.of(views);

                    public void refresh(Map<Position, Piece> board, Player player, Set<Piece> capturedPieces, Position source, Set<Position> targets) {
                        vi.forEach(v -> v.refresh(board, player, capturedPieces, source, targets));
                    }

                    public void announce(String message) {
                        vi.forEach(v -> v.announce(message));
                    }
                };
        }
    }

    public void play() {
        GameState state = initialState();
        views.refresh(state.getBoard(), state.getPlayer(), state.getCapturedPieces(), Position.OutOfBoard, HashSet.empty());

        while (!isOver(state)) {
            state = takeFirstInput(state);
            views.refresh(state.getBoard(), state.getPlayer(), state.getCapturedPieces(), Position.OutOfBoard, HashSet.empty());
        }
    }

    private GameState takeFirstInput(GameState state) {
        final Position source = inputs[state.getPlayer().ordinal()].takeInput();
        final Option<Piece> sourcePiece = state.getBoard().get(source).filter(p -> state.getPlayer().equals(p.getPlayer()));

        if (sourcePiece.isDefined()) {
            final Map<Position,Move> moves = sourcePiece.get().computePossibleMoves(source)
                    .filter(move -> move.validate(state.getBoard()))
                    .collect(HashMap.collector(Move::getTo));
            views.refresh(state.getBoard(), state.getPlayer(), state.getCapturedPieces(), source, moves.keySet());

            return takeSecondInput(state, source, moves);
        }

        if(source != Position.OutOfBoard) {
            views.announce("Invalid pickup: " + source);
        }
        return takeFirstInput(state);
    }

    private GameState takeSecondInput(GameState state, Position source, Map<Position, Move> moves) {
        final Position target = inputs[state.getPlayer().ordinal()].takeInput();

        if (moves.containsKey(target)) {
            views.announce(String.format("%s moves: %s - %s", state.getPlayer(), source, target));

            return moves.get(target).get().execute(state.getBoard(), state::advance, state::advance);
        }

        if(target == Position.OutOfBoard) {
            // You can put a piece back down in computer chess because perhaps the input is bogus
            views.announce("Cancel.");
            return takeFirstInput(state);
        }
        views.announce(String.format("Invalid move: %s - %s", source, target));
        return takeSecondInput(state, source, moves);
    }

    @VisibleForTesting
    GameState initialState() {
        return new GameState(Board.initialSetup(), White, HashSet.empty());
    }


    @VisibleForTesting
    boolean isOver(final GameState state) {
        // TODO The entire state is probably not needed
        // TODO We need a GameOverChecker :P
        return false;
    }
}
