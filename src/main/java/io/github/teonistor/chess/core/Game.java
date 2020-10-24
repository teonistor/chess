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
import lombok.val;

public class Game {

    private final GameStateProvider gameStateProvider;
    private final CheckRule checkRule;
    private final GameOverChecker gameOverChecker;
    private final Input[] inputs;
    private final View view;

    public Game(final GameStateProvider gameStateProvider, final CheckRule checkRule, final GameOverChecker gameOverChecker, final Input white, final Input black, final View view) {
        this.gameStateProvider = gameStateProvider;
        this.checkRule = checkRule;
        this.gameOverChecker = gameOverChecker;
        inputs = new Input[2];
        inputs[Player.White.ordinal()] = white;
        inputs[Player.Black.ordinal()] = black;
        this.view = view;
    }

    public void play() {
        var state = gameStateProvider.createState();

        while (true) {
            val possibleMoves = computeAvailableMoves(state);
            val gameCondition = gameOverChecker.check(state.getBoard(), state.getPlayer(), possibleMoves);
            view.refresh(state.getBoard(), state.getPlayer(), state.getCapturedPieces(), turnMovesIntoPairs(possibleMoves));

            switch (gameCondition) {
                case Continue:
                    state = processInput(possibleMoves);
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
    Map<Position, Map<Position, GameState>> computeAvailableMoves(final GameState state) {
        final Map<Position,Piece> board = state.getBoard();
        final Player player = state.getPlayer();

        return board.filterValues(piece -> piece.getPlayer() == player)
                .map((from, piece) -> new Tuple2<>(from, piece.computePossibleMoves(from)
                .filter(move -> move.validate(state))
                .map(move -> new Tuple2<>(move.getTo(), move.execute(state)))
                .filter(targetAndState -> checkRule.validate(targetAndState._2.getBoard(), player))
                .collect(HashMap.collector())));
    }

    @VisibleForTesting
    GameState processInput(final Map<Position, Map<Position,GameState>> possibleMoves) {
        final Tuple2<Position, Position> fromTo = inputs[0].simpleInput();

        return possibleMoves.get(fromTo._1)
              .flatMap(m -> m.get(fromTo._2))
              .getOrElse(() -> processInput(possibleMoves));
    }

    @VisibleForTesting
    Stream<Tuple2<Position, Position>> turnMovesIntoPairs(Map<Position, Map<Position, GameState>> possibleMoves) {
        return possibleMoves.toStream()
                .flatMap(m -> Stream.continually(m._1).zip(m._2.keySet()));
    }
}
