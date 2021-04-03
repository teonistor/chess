package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import io.github.teonistor.chess.util.NestedMapKeyExtractor;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static io.github.teonistor.chess.core.GameCondition.BlackWins;
import static io.github.teonistor.chess.core.GameCondition.Continue;
import static io.github.teonistor.chess.core.GameCondition.Stalemate;
import static io.github.teonistor.chess.core.GameCondition.WhiteWins;
import static io.github.teonistor.chess.core.Player.Black;
import static io.github.teonistor.chess.core.Player.White;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@MockitoSettings
class GameTest implements RandomPositionsTestMixin {
    private @Mock AvailableMovesRule rule;
    private @Mock GameOverChecker checker;
    private @Mock NestedMapKeyExtractor extractor;
    private @Mock View view;

    private @Mock GameState state;
    private @Mock GameState state2;
    private @Mock GameState state3;
    private @Mock Map<Position, Piece> board;

    @Test
    void constructWithGameStateProvider(final @Mock GameStateProvider provider) {
        when(provider.createState()).thenReturn(state);
        assertThat(new Game(rule, checker, extractor, view, provider).getState()).isEqualTo(state);
    }

    @ParameterizedTest(name="{0} {1}")
    @CsvSource({"Black,Continue",
                "White,BlackWins",
                "Black,WhiteWins",
                "White,Continue",
                "Black,Stalemate",
                "White,Stalemate"})
    void getCondition(final Player player, final GameCondition condition, final @Mock Map<Position, Map<Position, GameState>> availableMoves) {
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(player);
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(checker.check(board, player, availableMoves)).thenReturn(condition);

        final Game game = new Game(rule, checker, extractor, state);

        assertThat(game.getState()).isEqualTo(state);
        assertThat(game.getCondition()).isEqualTo(condition);
    }

    @Test
    void triggerViewOnContinueBlack(final @Mock Map<Position, Map<Position, GameState>> availableMoves, final @Mock Stream<Tuple2<Position, Position>> possibleMoves, final @Mock Piece piece1, final @Mock Piece piece2) {
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(Black);
        when(state.getCapturedPieces()).thenReturn(List.of(piece1, piece2));
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(checker.check(board, Black, availableMoves)).thenReturn(Continue);
        when(extractor.extract(availableMoves)).thenReturn(possibleMoves);

        new Game(rule, checker, extractor, state).triggerView(view);

        verify(view).refresh(board, List.of(piece1, piece2), possibleMoves, List.empty());
    }

    @Test
    void triggerViewOnContinueWhite(final @Mock Map<Position, Map<Position, GameState>> availableMoves, final @Mock Stream<Tuple2<Position, Position>> possibleMoves, final @Mock Piece piece1, final @Mock Piece piece2) {
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(White);
        when(state.getCapturedPieces()).thenReturn(List.of(piece1, piece2));
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(checker.check(board, White, availableMoves)).thenReturn(Continue);
        when(extractor.extract(availableMoves)).thenReturn(possibleMoves);

        new Game(rule, checker, extractor, state).triggerView(view);

        verify(view).refresh(board, List.of(piece1, piece2), List.empty(), possibleMoves);
    }

    @Test
    void triggerViewOnWhiteWins(final @Mock Map<Position, Map<Position, GameState>> availableMoves) {
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(Black);
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(checker.check(board, Black, availableMoves)).thenReturn(WhiteWins);

        new Game(rule, checker, extractor, state).triggerView(view);

        verify(view).announce("White wins!");
    }

    @Test
    void triggerViewOnBlackWins(final @Mock Map<Position, Map<Position, GameState>> availableMoves) {
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(White);
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(checker.check(board, White, availableMoves)).thenReturn(BlackWins);

        new Game(rule, checker, extractor, state).triggerView(view);

        verify(view).announce("Black wins!");
    }

    @ParameterizedTest(name="{0}")
    @EnumSource(Player.class)
    void triggerViewOnStalemate(final Player player, final @Mock Map<Position, Map<Position, GameState>> availableMoves) {
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(player);
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(checker.check(board, player, availableMoves)).thenReturn(Stalemate);

        new Game(rule, checker, extractor, state).triggerView(view);

        verify(view).announce("Stalemate!");
    }

    @ParameterizedTest(name="{0}")
    @EnumSource(Player.class)
    void processInputWhenGameOn(final Player player, final @Mock Map<Position, Map<Position, GameState>> availableMoves) {
        final Position from = randomPositions.next();
        final Position to = randomPositions.next();
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(player);
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(checker.check(board, player, availableMoves)).thenReturn(Continue);
        when(availableMoves.get(from)).thenReturn(Option.of(HashMap.of(to, state2)));

        final Game game = new Game(rule, checker, extractor, state);
        assertThat(game.processInput(from, to)).isEqualToComparingOnlyGivenFields(game, "availableMovesRule", "gameOverChecker", "nestedMapKeyExtractor")
                .extracting(Game::getState).isEqualTo(state2);
    }

    @ParameterizedTest(name="{0}")
    @EnumSource(Player.class)
    void processBadInputWhenGameOn(final Player player) {
        final Map<Position, Map<Position, GameState>> availableMoves = HashMap.of(
                randomPositions.next(), HashMap.of(randomPositions.next(), state2),
                randomPositions.next(), HashMap.of(randomPositions.next(), state3));
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(player);
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(checker.check(board, player, availableMoves)).thenReturn(Continue);

        final Game game = new Game(rule, checker, extractor, state);
        assertThat(game.processInput(randomPositions.next(), randomPositions.next())).isEqualTo(game);
    }

    @ParameterizedTest(name="{0} {1}")
    @CsvSource({"White,BlackWins",
                "Black,WhiteWins",
                "Black,Stalemate",
                "White,Stalemate"})
    void processInputWhenGameOver(final Player player, final GameCondition condition, final @Mock Map<Position, Map<Position, GameState>> availableMoves) {
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(player);
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(checker.check(board, player, availableMoves)).thenReturn(condition);

        final Game game = new Game(rule, checker, extractor, state);
        assertThat(game.processInput(randomPositions.next(), randomPositions.next())).isEqualTo(game);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(rule, checker, extractor, view, state, state2, state3, board);
    }
}