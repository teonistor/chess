package io.github.teonistor.chess.core;

import com.google.common.annotations.VisibleForTesting;
import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import lombok.Getter;

public class Game {

    // Mutable state!
    private @Getter GameState state;
    private Map<Position, Map<Position, GameState>> possibleMoves;
    private @Getter GameCondition condition;

    public Game(final GameStateProvider gameStateProvider) {
        this.state = gameStateProvider.createState();
        recomputeMovesAndCondition();
    }


    public Map<Position, Piece> getBoard() {
        return state.getBoard();
    }

    public Player getPlayer() {
        return state.getPlayer();
    }

    public List<Piece> getCapturedPieces() {
        return state.getCapturedPieces();
    }

    public Stream<Tuple2<Position,Position>> getPossibleMovePairs() {
        return possibleMoves.toStream().flatMap(m -> Stream.continually(m._1).zip(m._2.keySet()));
    }


    public void playRound(final Position source, final Position target, final View view) {
        executeMove(source, target, view);
        recomputeMovesAndCondition();

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
        condition = World.gameOverChecker().check(state.getBoard(), state.getPlayer(), possibleMoves);
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
                .filter(targetAndState -> World.checkRule().validate(targetAndState._2.getBoard(), player))
                .collect(HashMap.collector())));
    }

    private void executeMove(final Position source, final Position target, final View view) {
        final Option<GameState> move = possibleMoves.get(source).flatMap(m -> m.get(target));
        if (move.isDefined()) {
            view.announce(String.format("%s moves: %s - %s", state.getPlayer(), source, target));
            state = move.get();
        } else {
            view.announce(String.format("Invalid move: %s - %s", source, target));
        }
    }
}
