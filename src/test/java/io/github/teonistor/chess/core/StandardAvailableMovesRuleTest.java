package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.move.Move;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static io.github.teonistor.chess.board.Position.A1;
import static io.github.teonistor.chess.board.Position.A2;
import static io.github.teonistor.chess.board.Position.A3;
import static io.github.teonistor.chess.board.Position.B4;
import static io.github.teonistor.chess.board.Position.B5;
import static io.github.teonistor.chess.board.Position.D8;
import static io.github.teonistor.chess.board.Position.E5;
import static io.github.teonistor.chess.board.Position.H6;
import static io.github.teonistor.chess.core.GameStateKey.NIL;
import static io.github.teonistor.chess.core.Player.White;
import static java.util.function.UnaryOperator.identity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@MockitoSettings
class StandardAvailableMovesRuleTest {

    @Mock private Piece a1Piece;
    @Mock private Piece b4Piece;
    @Mock private Piece d8Piece;
    @Mock private Piece h6Piece;
    @Mock private Move move;
    @Mock private CheckRule checkRule;

    private GameState state;
    private AvailableMovesRule availableMovesRule;

    @BeforeEach
    void setUp() {
        final HashMap<Position, Piece> board = HashMap.of(A1, a1Piece, B4, b4Piece, D8, d8Piece, H6, h6Piece);
        state = spy(new GameState(board, White, List.empty(), null));
        availableMovesRule = new StandardAvailableMovesRule(checkRule);
    }

    @ParameterizedTest(name="{0}")
    @EnumSource(Player.class)
    void computeAvailableMoves(final Player currentPlayer) {
        final Player otherPlayer = currentPlayer.next();
        final Move selfFilteredMove = mock(Move.class);
        final Move ruleFilteredMove = mock(Move.class);
        final HashMap<Position, Piece> outputBoard = HashMap.of(A2, a1Piece);
        final HashMap<Position,Piece> ruleFilteredBoard = HashMap.of(B5, b4Piece);
        final GameState outputState = new GameState(outputBoard, otherPlayer, List.empty(), null);
        final GameState ruleFilteredState = new GameState(ruleFilteredBoard, otherPlayer, List.empty(), null);

        when(move.validate(state)).thenReturn(true);
        when(ruleFilteredMove.validate(state)).thenReturn(true);

        when(state.getPlayer()).thenReturn(currentPlayer);
        when(a1Piece.getPlayer()).thenReturn(currentPlayer);
        when(b4Piece.getPlayer()).thenReturn(otherPlayer);
        when(d8Piece.getPlayer()).thenReturn(currentPlayer);
        when(h6Piece.getPlayer()).thenReturn(otherPlayer);
        when(a1Piece.computePossibleMoves(A1)).thenReturn(Stream.of(move, selfFilteredMove));
        when(d8Piece.computePossibleMoves(D8)).thenReturn(Stream.of(selfFilteredMove, ruleFilteredMove));
        when(move.getTo()).thenReturn(A3);
        when(ruleFilteredMove.getTo()).thenReturn(E5);
        when(move.execute(state)).thenReturn(HashMap.of(identity(), outputState));
        when(ruleFilteredMove.execute(state)).thenReturn(HashMap.of(identity(), ruleFilteredState, (GameStateKey key) -> key.withPromotion(a1Piece), outputState));
        when(checkRule.check(outputBoard, currentPlayer)).thenReturn(false);
        when(checkRule.check(ruleFilteredBoard, currentPlayer)).thenReturn(true);

        assertThat(availableMovesRule.computeAvailableMoves(state)).isEqualTo(HashMap.of(
                NIL.withInput(currentPlayer, A1, A3), outputState,
                NIL.withInput(currentPlayer, D8, E5).withPromotion(a1Piece), outputState));
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(a1Piece, b4Piece, d8Piece, h6Piece, move, checkRule);
    }
}