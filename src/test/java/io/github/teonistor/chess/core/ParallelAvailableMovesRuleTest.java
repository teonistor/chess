package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.move.Move;
import io.github.teonistor.chess.piece.Bishop;
import io.github.teonistor.chess.piece.King;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import static io.github.teonistor.chess.core.GameStateKey.NIL;
import static io.github.teonistor.chess.core.Player.Black;
import static io.github.teonistor.chess.core.Player.White;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@MockitoSettings
class ParallelAvailableMovesRuleTest implements RandomPositionsTestMixin {

    // ParallelAvailableMovesRule::validateBoardwideRules cares about kings in particular, but all they need is to exist. So we might as well use kings as our mock pieces
    @Mock private King whitePiece;
    @Mock private King blackPiece;
    @Mock private Move whiteMove;
    @Mock private Move blackMove;

    @Mock private CheckRule checkRule;
    @Mock private UnderAttackRule underAttackRule;

    private AvailableMovesRule availableMovesRule;

    @BeforeEach
    void setUp() {
        availableMovesRule = new ParallelAvailableMovesRule(checkRule);
    }

    @Test
    void computeAvailableMoves() {
        when(whitePiece.getPlayer()).thenReturn(White);
        when(blackPiece.getPlayer()).thenReturn(Black);

        final Position initialWhitePosition = randomPositions.next();
        final Position finalWhitePosition = randomPositions.next();
        final Position initialBlackPosition = randomPositions.next();
        final Position finalBlackPosition = randomPositions.next();

        final HashMap<Position, Piece> initialBoard = HashMap.of(initialWhitePosition, whitePiece, initialBlackPosition, blackPiece);
        final HashMap<Position, Piece> boardAfterWhiteMove = HashMap.of(finalWhitePosition, whitePiece, initialBlackPosition, blackPiece);
        final HashMap<Position, Piece> boardAfterBlackMove = HashMap.of(initialWhitePosition, whitePiece, finalBlackPosition, blackPiece);
        final HashMap<Position, Piece> finalBoard = HashMap.of(finalWhitePosition, whitePiece, finalBlackPosition, blackPiece);

        final GameState initialState = new GameState(initialBoard, White, List.empty(), null);
        final GameState stateAfterWhiteMove = new GameState(boardAfterWhiteMove, Black, List.empty(), null);
        final GameState stateAfterBlackMove = new GameState(boardAfterBlackMove, White, List.empty(), null);
        final GameState stateAfterWhiteThenBlackMove = new GameState(finalBoard, White, List.empty(), null);
        final GameState stateAfterBlackThenWhiteMove = new GameState(finalBoard, Black, List.empty(), null);

        when(whitePiece.computePossibleMoves(initialWhitePosition)).thenReturn(Stream.of(whiteMove)).thenReturn(Stream.of(whiteMove));
        when(blackPiece.computePossibleMoves(initialBlackPosition)).thenReturn(Stream.of(blackMove)).thenReturn(Stream.of(blackMove));

        when(whiteMove.validate(initialState)).thenReturn(true);
        when(blackMove.validate(initialState.withPlayer(Black))).thenReturn(true);
        when(whiteMove.validate(stateAfterBlackMove)).thenReturn(true);
        when(blackMove.validate(stateAfterWhiteMove)).thenReturn(true);

        when(whiteMove.execute(initialState)).thenReturn(stateAfterWhiteMove);
        when(blackMove.execute(initialState.withPlayer(Black))).thenReturn(stateAfterBlackMove);
        when(whiteMove.execute(stateAfterBlackMove)).thenReturn(stateAfterBlackThenWhiteMove);
        when(blackMove.execute(stateAfterWhiteMove)).thenReturn(stateAfterWhiteThenBlackMove);

        when(checkRule.check(boardAfterBlackMove, Black)).thenReturn(false);
        when(checkRule.check(boardAfterWhiteMove, White)).thenReturn(false);
        when(checkRule.check(finalBoard, Black)).thenReturn(false);
        when(checkRule.check(finalBoard, White)).thenReturn(false);

        when(whiteMove.getTo()).thenReturn(finalWhitePosition);
        when(blackMove.getTo()).thenReturn(finalBlackPosition);

        final GameState result = availableMovesRule.computeAvailableMoves(initialState).get(
                NIL.withBlackInput(initialBlackPosition, finalBlackPosition).withWhiteInput(initialWhitePosition, finalWhitePosition)).get();

        assertThat(result.getBoard()).isEqualTo(HashMap.of(finalWhitePosition, whitePiece, finalBlackPosition, blackPiece));
    }

    @Test
    void computeAvailableMovesWithConflict() {
        when(whitePiece.getPlayer()).thenReturn(White);
        when(blackPiece.getPlayer()).thenReturn(Black);

        final Position initialWhitePosition = randomPositions.next();
        final Position whitePositionIfMovedFirst = randomPositions.next();
        final Position whitePositionIfMovedSecond = randomPositions.next();
        final Position initialBlackPosition = randomPositions.next();
        final Position blackPositionIfMovedFirst = randomPositions.next();
        final Position blackPositionIfMovedSecond = randomPositions.next();

        final HashMap<Position, Piece> initialBoard = HashMap.of(initialWhitePosition, whitePiece, initialBlackPosition, blackPiece);
        final HashMap<Position, Piece> boardAfterWhiteMove = HashMap.of(whitePositionIfMovedFirst, whitePiece, initialBlackPosition, blackPiece);
        final HashMap<Position, Piece> boardAfterBlackMove = HashMap.of(initialWhitePosition, whitePiece, blackPositionIfMovedFirst, blackPiece);
        final HashMap<Position, Piece> boardAfterWhiteThenBlackMove = HashMap.of(whitePositionIfMovedFirst, whitePiece, blackPositionIfMovedSecond, blackPiece);
        final HashMap<Position, Piece> boardAfterBlackThenWhiteMove = HashMap.of(whitePositionIfMovedSecond, whitePiece, blackPositionIfMovedFirst, blackPiece);

        final GameState initialState = new GameState(initialBoard, White, List.empty(), null);
        final GameState stateAfterWhiteMove = new GameState(boardAfterWhiteMove, Black, List.empty(), null);
        final GameState stateAfterBlackMove = new GameState(boardAfterBlackMove, White, List.empty(), null);
        final GameState stateAfterWhiteThenBlackMove = new GameState(boardAfterWhiteThenBlackMove, White, List.empty(), null);
        final GameState stateAfterBlackThenWhiteMove = new GameState(boardAfterBlackThenWhiteMove, Black, List.empty(), null);

        when(whitePiece.computePossibleMoves(initialWhitePosition)).thenReturn(Stream.of(whiteMove)).thenReturn(Stream.of(whiteMove));
        when(blackPiece.computePossibleMoves(initialBlackPosition)).thenReturn(Stream.of(blackMove)).thenReturn(Stream.of(blackMove));

        when(whiteMove.validate(initialState)).thenReturn(true);
        when(blackMove.validate(initialState.withPlayer(Black))).thenReturn(true);
        when(whiteMove.validate(stateAfterBlackMove)).thenReturn(true);
        when(blackMove.validate(stateAfterWhiteMove)).thenReturn(true);

        when(whiteMove.execute(initialState)).thenReturn(stateAfterWhiteMove);
        when(blackMove.execute(initialState.withPlayer(Black))).thenReturn(stateAfterBlackMove);
        when(whiteMove.execute(stateAfterBlackMove)).thenReturn(stateAfterBlackThenWhiteMove);
        when(blackMove.execute(stateAfterWhiteMove)).thenReturn(stateAfterWhiteThenBlackMove);

        when(checkRule.check(boardAfterBlackMove, Black)).thenReturn(false);
        when(checkRule.check(boardAfterWhiteMove, White)).thenReturn(false);
        when(checkRule.check(boardAfterWhiteThenBlackMove, Black)).thenReturn(false);
        when(checkRule.check(boardAfterBlackThenWhiteMove, White)).thenReturn(false);

        when(whiteMove.getTo()).thenReturn(whitePositionIfMovedFirst);
        when(blackMove.getTo()).thenReturn(blackPositionIfMovedFirst);

        assertThat(availableMovesRule.computeAvailableMoves(initialState)).isEmpty();
    }

    @ParameterizedTest
    @EnumSource(Player.class)
    void kinglessBoardIsBoardwideInvalid(Player player) {
        assertThat(availableMovesRule.validateBoardwideRules(player, HashMap.of(randomPositions.next(), new King(player.next(), underAttackRule), randomPositions.next(), new Bishop(player)))).isFalse();
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(whitePiece, blackPiece, whiteMove, blackMove, checkRule, underAttackRule);
    }
}