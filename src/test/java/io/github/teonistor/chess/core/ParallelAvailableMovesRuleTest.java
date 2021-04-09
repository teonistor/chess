package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.move.Move;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.stream.Stream;

import static io.github.teonistor.chess.core.GameStateKey.NIL;
import static io.github.teonistor.chess.core.Player.Black;
import static io.github.teonistor.chess.core.Player.White;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@MockitoSettings
class ParallelAvailableMovesRuleTest implements RandomPositionsTestMixin {

    @Mock private Piece whitePiece;
    @Mock private Piece blackPiece;
    @Mock private Move whiteMove;
    @Mock private Move blackMove;

    @Mock private CheckRule checkRule;

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
        final Position whitePositionIfMovedFirst = randomPositions.next();
        final Position initialBlackPosition = randomPositions.next();
        final Position blackPositionIfMovedFirst = randomPositions.next();

        final HashMap<Position, Piece> initialBoard = HashMap.of(initialWhitePosition, whitePiece, initialBlackPosition, blackPiece);
        final HashMap<Position, Piece> boardAfterWhiteMove = HashMap.of(whitePositionIfMovedFirst, whitePiece, initialBlackPosition, blackPiece);
        final HashMap<Position, Piece> boardAfterBlackMove = HashMap.of(initialWhitePosition, whitePiece, blackPositionIfMovedFirst, blackPiece);
        final HashMap<Position, Piece> boardAfterWhiteThenBlackMove = HashMap.of(whitePositionIfMovedFirst, whitePiece, blackPositionIfMovedFirst, blackPiece);
        final HashMap<Position, Piece> boardAfterBlackThenWhiteMove = HashMap.of(whitePositionIfMovedFirst, whitePiece, blackPositionIfMovedFirst, blackPiece);

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

        final GameState result = availableMovesRule.computeAvailableMoves(initialState).get(
                NIL.withBlackInput(initialBlackPosition, blackPositionIfMovedFirst).withWhiteInput(initialWhitePosition, whitePositionIfMovedFirst)).get();

        assertThat(result.getBoard()).isEqualTo(HashMap.of(whitePositionIfMovedFirst, whitePiece, blackPositionIfMovedFirst, blackPiece));
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

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(whitePiece, blackPiece, whiteMove, blackMove, checkRule);
    }
}