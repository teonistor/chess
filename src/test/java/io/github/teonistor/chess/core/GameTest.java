package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.Rook;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import io.github.teonistor.chess.util.PositionPairExtractor;
import io.github.teonistor.chess.util.PromotionRequirementExtractor;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
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
    private @Mock PositionPairExtractor pairExtractor;
    private @Mock PromotionRequirementExtractor promotionExtractor;
    private @Mock View view;

    private @Mock GameState state;
    private @Mock GameState state2;
    private @Mock GameState state3;
    private @Mock Map<Position, Piece> board;
    private @Mock Map<GameStateKey,GameState> availableMoves;


    @ParameterizedTest(name="{0} {1}")
    @CsvSource({"Black,Continue",
                "White,BlackWins",
                "Black,WhiteWins",
                "White,Continue",
                "Black,Stalemate",
                "White,Stalemate"})
    void getCondition(final Player player, final GameCondition condition) {
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(player);
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(checker.check(board, player, availableMoves)).thenReturn(condition);

        final Game game = new Game(rule, checker, pairExtractor, promotionExtractor, state);

        assertThat(game.getState()).isEqualTo(state);
        assertThat(game.getCondition()).isEqualTo(condition);
    }

    @Test
    void triggerViewOnContinue(final @Mock Set<Tuple2<Position, Position>> possibleMovesBlack, final @Mock Set<Tuple2<Position, Position>> possibleMovesWhite, final @Mock Piece piece1, final @Mock Piece piece2) {
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(Black);
        when(state.getCapturedPieces()).thenReturn(List.of(piece1, piece2));
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(checker.check(board, Black, availableMoves)).thenReturn(Continue);
        when(pairExtractor.extractBlack(availableMoves)).thenReturn(possibleMovesBlack);
        when(pairExtractor.extractWhite(availableMoves)).thenReturn(possibleMovesWhite);
        when(promotionExtractor.extractBlack(GameStateKey.NIL, availableMoves)).thenReturn(true);
        when(promotionExtractor.extractWhite(GameStateKey.NIL, availableMoves)).thenReturn(false);

        new Game(rule, checker, pairExtractor, promotionExtractor, state).triggerView(view);

        verify(view).refresh(board, List.of(piece1, piece2), possibleMovesBlack, possibleMovesWhite, true, false);
    }

    @Test
    @Disabled("We will probably want to always trigger")
    void triggerViewOnContinueAndPartialMoveDone() {
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(Black);
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(checker.check(board, Black, availableMoves)).thenReturn(Continue);

        // 2 in 1 tests. The test here is that view::refresh is not called
        new Game(rule, checker, pairExtractor, promotionExtractor, state, GameStateKey.NIL.withWhiteInput(randomPositions.next(), randomPositions.next())).triggerView(view);
        new Game(rule, checker, pairExtractor, promotionExtractor, state, GameStateKey.NIL.withBlackInput(randomPositions.next(), randomPositions.next())).triggerView(view);
    }

    @Test
    void triggerViewOnWhiteWins() {
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(Black);
        when(state.getCapturedPieces()).thenReturn(List.empty());
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(pairExtractor.extractBlack(availableMoves)).thenReturn(HashSet.empty());
        when(pairExtractor.extractWhite(availableMoves)).thenReturn(HashSet.empty());
        when(promotionExtractor.extractBlack(GameStateKey.NIL, availableMoves)).thenReturn(false);
        when(promotionExtractor.extractWhite(GameStateKey.NIL, availableMoves)).thenReturn(false);
        when(checker.check(board, Black, availableMoves)).thenReturn(WhiteWins);

        new Game(rule, checker, pairExtractor, promotionExtractor, state).triggerView(view);

        verify(view).refresh(board, List.empty(), HashSet.empty(), HashSet.empty(), false, false);
        verify(view).announce("White wins!");
    }

    @Test
    void triggerViewOnBlackWins() {
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(White);
        when(state.getCapturedPieces()).thenReturn(List.empty());
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(pairExtractor.extractBlack(availableMoves)).thenReturn(HashSet.empty());
        when(pairExtractor.extractWhite(availableMoves)).thenReturn(HashSet.empty());
        when(promotionExtractor.extractBlack(GameStateKey.NIL, availableMoves)).thenReturn(false);
        when(promotionExtractor.extractWhite(GameStateKey.NIL, availableMoves)).thenReturn(false);
        when(checker.check(board, White, availableMoves)).thenReturn(BlackWins);

        new Game(rule, checker, pairExtractor, promotionExtractor, state).triggerView(view);

        verify(view).announce("Black wins!");
        verify(view).refresh(board, List.empty(), HashSet.empty(), HashSet.empty(), false, false);
    }

    @ParameterizedTest(name="{0}")
    @EnumSource(Player.class)
    void triggerViewOnStalemate(final Player player) {
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(player);
        when(state.getCapturedPieces()).thenReturn(List.empty());
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(pairExtractor.extractBlack(availableMoves)).thenReturn(HashSet.empty());
        when(pairExtractor.extractWhite(availableMoves)).thenReturn(HashSet.empty());
        when(promotionExtractor.extractBlack(GameStateKey.NIL, availableMoves)).thenReturn(false);
        when(promotionExtractor.extractWhite(GameStateKey.NIL, availableMoves)).thenReturn(false);
        when(checker.check(board, player, availableMoves)).thenReturn(Stalemate);

        new Game(rule, checker, pairExtractor, promotionExtractor, state).triggerView(view);

        verify(view).announce("Stalemate!");
        verify(view).refresh(board, List.empty(), HashSet.empty(), HashSet.empty(), false, false);
    }

    @ParameterizedTest(name="{0}")
    @EnumSource(Player.class)
    void processInputWhenGameOn(final Player player, final @Mock Piece piece) {
        final Position from = randomPositions.next();
        final Position to = randomPositions.next();
        final Map<GameStateKey,GameState> availableMoves = HashMap.of(GameStateKey.NIL.withInput(player, from, to), state2);
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(player);
        when(board.get(from)).thenReturn(Option.some(piece));
        when(piece.getPlayer()).thenReturn(player);
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(checker.check(board, player, availableMoves)).thenReturn(Continue);

        final Game game = new Game(rule, checker, pairExtractor, promotionExtractor, state);
        assertThat(game.processInput(from, to)).isEqualToComparingOnlyGivenFields(game, "availableMovesRule", "gameOverChecker", "positionPairExtractor", "key")
                .extracting(Game::getState).isEqualTo(state2);
    }

    @ParameterizedTest(name="{0}")
    @EnumSource(Player.class)
    void processInputWhenGameOnButMoreInputIsNeeded(final Player player, final @Mock Piece piece) {
        final Position from = randomPositions.next();
        final Position to = randomPositions.next();
        final Map<GameStateKey,GameState> availableMoves = HashMap.of(GameStateKey.NIL.withInput(player, from, to).withWhitePromotion(piece), state2);
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(player);
        when(board.get(from)).thenReturn(Option.some(piece));
        when(piece.getPlayer()).thenReturn(player);
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(checker.check(board, player, availableMoves)).thenReturn(Continue);

        final Game game = new Game(rule, checker, pairExtractor, promotionExtractor, state);
        assertThat(game.processInput(from, to)).isEqualToComparingOnlyGivenFields(game, "availableMovesRule", "gameOverChecker", "positionPairExtractor", "state")
                .extracting("key").isEqualTo(GameStateKey.NIL.withInput(player, from, to));
    }

    @Test
    void processBadInputWhenGameOn() {
        final Position from = randomPositions.next();
        when(state.getBoard()).thenReturn(board);
        when(board.get(from)).thenReturn(Option.none());

        final Game game = new Game(rule, checker, pairExtractor, promotionExtractor, state);
        assertThat(game.processInput(from, randomPositions.next())).isEqualToComparingOnlyGivenFields(game, "availableMovesRule", "gameOverChecker", "positionPairExtractor", "state")
                .extracting("key").isEqualTo(GameStateKey.NIL);
    }

    @ParameterizedTest(name="{0}")
    @EnumSource(Player.class)
    void processPromotionInput(final Player player) {
        final Position from = randomPositions.next();
        final Position to = randomPositions.next();
        final GameStateKey key = GameStateKey.NIL.withInput(player, from, to);
        final Map<GameStateKey, GameState> availableMoves = HashMap.of(key.withPromotion(new Rook(player)), state2);
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(player);
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(checker.check(board, player, availableMoves)).thenReturn(Continue);

        final Game game = new Game(rule, checker, pairExtractor, promotionExtractor, state, key);
        assertThat(game.processInput(new Rook(player))).isEqualToComparingOnlyGivenFields(game, "availableMovesRule", "gameOverChecker", "positionPairExtractor")
                .extracting("state", "key").containsExactly(state2, GameStateKey.NIL);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(rule, checker, pairExtractor, promotionExtractor, view, state, state2, state3, board, availableMoves);
    }
}