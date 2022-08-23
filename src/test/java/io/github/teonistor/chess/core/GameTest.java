package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.Rook;
import io.github.teonistor.chess.rule.AvailableMovesRule;
import io.github.teonistor.chess.rule.GameOverChecker;
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
import static io.github.teonistor.chess.factory.Factory.GameType.PARALLEL;
import static io.github.teonistor.chess.factory.Factory.GameType.STANDARD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

        final Game game = new Game(rule, checker, pairExtractor, promotionExtractor, STANDARD, state);

        assertThat(game.getState()).isEqualTo(state);
        assertThat(game.getCondition()).isEqualTo(condition);
    }

    @Test
    void triggerViewOnContinueInStandardGame(final @Mock Set<Tuple2<Position, Position>> possibleMovesBlack, final @Mock Set<Tuple2<Position, Position>> possibleMovesWhite, final @Mock Piece piece1, final @Mock Piece piece2, final @Mock GameState previousState) {
        final Position pos1 = randomPositions.next();
        final Position pos2 = randomPositions.next();
        final Position pos3 = randomPositions.next();
        final HashMap<Position, Piece> board = HashMap.of(pos1, piece1, pos2, piece2);

        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(Black);
        when(state.getCapturedPieces()).thenReturn(List.of(piece1, piece2));
        when(state.getPrevious()).thenReturn(previousState);
        when(previousState.getBoard()).thenReturn(HashMap.of(pos1, piece1, pos3, piece2));
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(checker.check(board, Black, availableMoves)).thenReturn(Continue);
        when(pairExtractor.extractBlack(availableMoves)).thenReturn(possibleMovesBlack);
        when(pairExtractor.extractWhite(availableMoves)).thenReturn(possibleMovesWhite);
        when(promotionExtractor.extractBlack(GameStateKey.NIL, availableMoves)).thenReturn(true);
        when(promotionExtractor.extractWhite(GameStateKey.NIL, availableMoves)).thenReturn(false);

        new Game(rule, checker, pairExtractor, promotionExtractor, STANDARD, state).triggerView(view);

        verify(view).refresh(board, List.of(piece1, piece2), HashSet.of(pos2, pos3), possibleMovesBlack, possibleMovesWhite, true, false);
    }

    @Test
    void triggerViewOnContinueInParallelGame(final @Mock Set<Tuple2<Position, Position>> possibleMovesBlack, final @Mock Set<Tuple2<Position, Position>> possibleMovesWhite, final @Mock Piece piece1, final @Mock Piece piece2, final @Mock GameState previousState, final @Mock GameState twoStatesAgo) {
        final Position pos1 = randomPositions.next();
        final Position pos2 = randomPositions.next();
        final Position pos3 = randomPositions.next();
        final Position pos4 = randomPositions.next();
        final HashMap<Position, Piece> board = HashMap.of(pos1, piece1, pos2, piece2);

        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(Black);
        when(state.getCapturedPieces()).thenReturn(List.of(piece1, piece2));
        when(state.getPrevious()).thenReturn(previousState);
        when(previousState.getPrevious()).thenReturn(twoStatesAgo);
        when(twoStatesAgo.getBoard()).thenReturn(HashMap.of(pos3, piece1, pos4, piece2));
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(checker.check(board, Black, availableMoves)).thenReturn(Continue);
        when(pairExtractor.extractBlack(availableMoves)).thenReturn(possibleMovesBlack);
        when(pairExtractor.extractWhite(availableMoves)).thenReturn(possibleMovesWhite);
        when(promotionExtractor.extractBlack(GameStateKey.NIL, availableMoves)).thenReturn(true);
        when(promotionExtractor.extractWhite(GameStateKey.NIL, availableMoves)).thenReturn(false);

        new Game(rule, checker, pairExtractor, promotionExtractor, PARALLEL, state).triggerView(view);

        verify(view).refresh(board, List.of(piece1, piece2), HashSet.of(pos1,pos2,pos3,pos4), possibleMovesBlack, possibleMovesWhite, true, false);
    }

    @Test
    void triggerViewOnWhiteWins() {
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(Black);
        when(state.getCapturedPieces()).thenReturn(List.empty());
        when(state.getPrevious()).thenReturn(null);
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(pairExtractor.extractBlack(availableMoves)).thenReturn(HashSet.empty());
        when(pairExtractor.extractWhite(availableMoves)).thenReturn(HashSet.empty());
        when(promotionExtractor.extractBlack(GameStateKey.NIL, availableMoves)).thenReturn(false);
        when(promotionExtractor.extractWhite(GameStateKey.NIL, availableMoves)).thenReturn(false);
        when(checker.check(board, Black, availableMoves)).thenReturn(WhiteWins);

        new Game(rule, checker, pairExtractor, promotionExtractor, STANDARD, state).triggerView(view);

        verify(view).refresh(board, List.empty(), HashSet.empty(), HashSet.empty(), HashSet.empty(), false, false);
        verify(view).announce("White wins!");
    }

    @Test
    void triggerViewOnBlackWins() {
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(White);
        when(state.getCapturedPieces()).thenReturn(List.empty());
        when(state.getPrevious()).thenReturn(null);
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(pairExtractor.extractBlack(availableMoves)).thenReturn(HashSet.empty());
        when(pairExtractor.extractWhite(availableMoves)).thenReturn(HashSet.empty());
        when(promotionExtractor.extractBlack(GameStateKey.NIL, availableMoves)).thenReturn(false);
        when(promotionExtractor.extractWhite(GameStateKey.NIL, availableMoves)).thenReturn(false);
        when(checker.check(board, White, availableMoves)).thenReturn(BlackWins);

        new Game(rule, checker, pairExtractor, promotionExtractor, STANDARD, state).triggerView(view);

        verify(view).announce("Black wins!");
        verify(view).refresh(board, List.empty(), HashSet.empty(), HashSet.empty(), HashSet.empty(), false, false);
    }

    @ParameterizedTest(name="{0}")
    @EnumSource(Player.class)
    void triggerViewOnStalemate(final Player player) {
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(player);
        when(state.getCapturedPieces()).thenReturn(List.empty());
        when(state.getPrevious()).thenReturn(null);
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(pairExtractor.extractBlack(availableMoves)).thenReturn(HashSet.empty());
        when(pairExtractor.extractWhite(availableMoves)).thenReturn(HashSet.empty());
        when(promotionExtractor.extractBlack(GameStateKey.NIL, availableMoves)).thenReturn(false);
        when(promotionExtractor.extractWhite(GameStateKey.NIL, availableMoves)).thenReturn(false);
        when(checker.check(board, player, availableMoves)).thenReturn(Stalemate);

        new Game(rule, checker, pairExtractor, promotionExtractor, STANDARD, state).triggerView(view);

        verify(view).announce("Stalemate!");
        verify(view).refresh(board, List.empty(), HashSet.empty(), HashSet.empty(), HashSet.empty(), false, false);
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
        when(rule.computeAvailableMoves(any())).thenReturn(null);
        when(rule.computeAvailableMoves(state)).thenReturn(availableMoves);
        when(checker.check(any(), any(), any())).thenReturn(null);
        when(checker.check(board, player, availableMoves)).thenReturn(Continue);

        /* We're mildly stuck fixing 4 tests here
         * - The recursive comparison seems to look at Lazy fields although they're not listed (unless I exclude Lazy.class)
         * - Had to split the assertion into 2, but OK.
         * - Mocks are now strict and we have unverified interactions on at least rule, checker, state, state2...
         * TODO Revisit after the re-re-(re?)-refactor because it might help with the structure anyway
         * Current attempt e.g.
         *     final Game actual = game.processInput(from, to);
         *     assertThat(actual).extracting(Game::getState).isEqualTo(state2);
         *     assertThat(actual).usingRecursiveComparison(RecursiveComparisonConfiguration.builder()
         *              .withIgnoredFieldsOfTypes(Lazy.class)
         *              .withComparedFields("availableMovesRule", "gameOverChecker", "positionPairExtractor", "promotionRequirementExtractor", "type", "key").build())
         *        .isEqualTo(game);
         */

        final Game game = new Game(rule, checker, pairExtractor, promotionExtractor, STANDARD, state);
        assertThat(game.processInput(from, to)).isEqualToComparingOnlyGivenFields(game, "availableMovesRule", "gameOverChecker", "positionPairExtractor", "promotionRequirementExtractor", "type", "key")
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

        final Game game = new Game(rule, checker, pairExtractor, promotionExtractor, STANDARD, state);
        assertThat(game.processInput(from, to)).isEqualToComparingOnlyGivenFields(game, "availableMovesRule", "gameOverChecker", "positionPairExtractor", "promotionRequirementExtractor", "type", "state")
                .extracting("key").isEqualTo(GameStateKey.NIL.withInput(player, from, to));
    }

    @Test
    void processBadInputWhenGameOn() {
        final Position from = randomPositions.next();
        when(state.getBoard()).thenReturn(board);
        when(board.get(from)).thenReturn(Option.none());

        final Game game = new Game(rule, checker, pairExtractor, promotionExtractor, STANDARD, state);
        assertThat(game.processInput(from, randomPositions.next())).isEqualToComparingOnlyGivenFields(game, "availableMovesRule", "gameOverChecker", "positionPairExtractor", "promotionRequirementExtractor", "type", "state")
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

        final Game game = new Game(rule, checker, pairExtractor, promotionExtractor, PARALLEL, state, key);
        assertThat(game.processInput(new Rook(player))).isEqualToComparingOnlyGivenFields(game, "availableMovesRule", "gameOverChecker", "positionPairExtractor", "promotionRequirementExtractor", "type")
                .extracting("state", "key").containsExactly(state2, GameStateKey.NIL);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(rule, checker, pairExtractor, promotionExtractor, view, state, state2, state3, board, availableMoves);
    }
}