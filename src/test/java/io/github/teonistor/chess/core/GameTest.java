package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.move.Move;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;

import java.util.function.Function;
import java.util.stream.Stream;

import static io.github.teonistor.chess.board.Position.A1;
import static io.github.teonistor.chess.board.Position.A3;
import static io.github.teonistor.chess.board.Position.B3;
import static io.github.teonistor.chess.board.Position.G7;
import static io.github.teonistor.chess.board.Position.OutOfBoard;
import static io.github.teonistor.chess.core.Player.White;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class GameTest {

    private final InitialStateProvider provider = mock(InitialStateProvider.class);
    private final CheckRule rule = mock(CheckRule.class);
    private final GameOverChecker checker = mock(GameOverChecker.class);
    private final Input white = mock(Input.class);
    private final Input black = mock(Input.class);
    private final View view = mock(View.class);
    private final Piece piece = mock(Piece.class);
    private final HashMap<Position, Piece> board = HashMap.of(A1, piece);
    private final GameState state = spy(new GameState(board, White, HashSet.empty()));
    private final Move move = mock(Move.class);

    // These stubs are a necessary evil if we are to test that the game indeed passes a state::advance method reference (verified on the state mock)
    @SuppressWarnings("unchecked")
    private final Answer<GameState> moveStub = invocation -> {
        ((Function<Map<Position, Piece>, Map<Position, Piece>>) invocation.getArgument(1)).apply(invocation.getArgument(0));
        return state;
    };

    private Game game;

    @BeforeEach
    void setUp() {
        game = spy(new Game(provider, rule, checker, white, black, view));
        when(provider.createInitialState()).thenReturn(state);
        when(rule.validate(any(),any())).thenReturn(true);
        when(state.advance(any())).thenReturn(state);
    }

    @ParameterizedTest
    @ValueSource(ints={1, 2, 7, 25})
    void loop(int howManyLoops) {
        when(state.getBoard()).thenReturn(board);
        when(state.getPlayer()).thenReturn(White);
        // Bloody hell don't use the when-return notation with actioning spies!
        doReturn(state).when(game).playOne(state);
        OngoingStubbing<Boolean> checkerStub = when(checker.isOver(board, White));
        for (int i = 1; i < howManyLoops; i++) {
            checkerStub = checkerStub.thenReturn(false);
        }
        checkerStub.thenReturn(true);

        game.play();

        verify(provider).createInitialState();
        verify(view, times(howManyLoops)).refresh(board, White, HashSet.empty(), OutOfBoard, HashSet.empty());
        verify(state, times(howManyLoops * 2)).getBoard();
        verify(state, times(howManyLoops * 2)).getPlayer();
        verify(checker, times(howManyLoops)).isOver(board, White);
        verify(game, times(howManyLoops - 1)).playOne(state);
    }

    @Test
    void playOne() {

        org.junit.jupiter.api.Assumptions.assumeTrue(false, "TODO");

        when(white.takeInput()).thenReturn(A3).thenReturn(A1).thenReturn(G7).thenReturn(B3);
        when(piece.getPlayer()).thenReturn(White);
        when(piece.computePossibleMoves(A1)).thenReturn(Stream.of(move));
        when(move.validate(any())).thenReturn(true);
        when(move.getTo()).thenReturn(B3);
        when(move.execute(eq(board), any(), any())).then(moveStub);
        game.play();

        verify(provider).createInitialState();
        verify(checker, times(2)).isOver(board, White);
        verify(white, times(4)).takeInput();
        verify(view, times(3)).refresh(eq(board), any(), eq(HashSet.empty()), any(), any());
        verify(view).announce("Invalid pickup: A3");
        verify(view).announce("Invalid move: A1 - G7");
        verify(view).announce("White moves: A1 - B3");
        verify(piece).getPlayer();
        verify(piece).computePossibleMoves(A1);
        verify(move).validate(board);
        verify(move).getTo();
        verify(move).execute(eq(board), any(), any());
        verify(state).advance(board);
    }

    // TODO Game deserves, like, a couple dozen tests

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(provider, rule, checker, white, black, view, piece, move);
    }
}