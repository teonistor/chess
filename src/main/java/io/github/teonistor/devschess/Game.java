package io.github.teonistor.devschess;

import com.google.common.annotations.VisibleForTesting;
import io.github.teonistor.devschess.board.Board;
import io.github.teonistor.devschess.board.Position;
import io.github.teonistor.devschess.inter.Input;
import io.github.teonistor.devschess.inter.View;
import io.github.teonistor.devschess.move.Move;
import io.github.teonistor.devschess.piece.Piece;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.control.Option;

import static io.github.teonistor.devschess.Player.White;

public class Game {

    private final Input[] inputs;
    private final View views;

    public Game(Input white, Input black, View...views) {
        inputs = new Input[2];
        inputs[Player.White.ordinal()] = white;
        inputs[Player.Black.ordinal()] = black;

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
                    .filter(move -> move.validate(state.getBoard().toJavaMap()))
                    .collect(HashMap.collector(Move::getTo));
            views.refresh(state.getBoard(), state.getPlayer(), state.getCapturedPieces(), source, moves.keySet());

            return takeSecondInput(state, source, sourcePiece.get(), moves);
        }

        if(source != Position.OutOfBoard) {
            views.announce("Invalid pickup: " + source);
        }
        return takeFirstInput(state);
    }

    private GameState takeSecondInput(GameState state, Position source, Piece sourcePiece, Map<Position, Move> moves) {
        final Position target = inputs[state.getPlayer().ordinal()].takeInput();

        if (moves.containsKey(target)) {
            views.announce(String.format("%s moves: %s - %s", state.getPlayer(), source, target));

            // TODO captures
            return state.advance(bridgeTheGap(moves.get(target).get(), state.getBoard()));
        }

        if(target == Position.OutOfBoard) {
            // You can put a piece back down in computer chess because perhaps the input is bogus
            views.announce("Cancel.");
            return takeFirstInput(state);
        }
        views.announce(String.format("Invalid move: %s - %s", source, target));
        return takeSecondInput(state, source, sourcePiece, moves);
    }

    private HashMap<Position, Piece> bridgeTheGap(Move move, Map<Position, Piece> board) {
        final java.util.Map<Position, Piece> tmp = board.toJavaMap();
        move.execute(tmp);
        return HashMap.ofAll(tmp);
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
