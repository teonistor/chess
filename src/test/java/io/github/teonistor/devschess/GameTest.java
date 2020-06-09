package io.github.teonistor.devschess;

import io.github.teonistor.devschess.board.Position;
import io.github.teonistor.devschess.inter.Input;
import io.github.teonistor.devschess.inter.View;
import io.github.teonistor.devschess.move.Move;
import io.github.teonistor.devschess.piece.Piece;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static io.github.teonistor.devschess.Player.White;
import static io.github.teonistor.devschess.board.Position.A1;
import static io.github.teonistor.devschess.board.Position.A3;
import static io.github.teonistor.devschess.board.Position.B3;
import static io.github.teonistor.devschess.board.Position.G7;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class GameTest {

    private final Input white = mock(Input.class);
    private final Input black = mock(Input.class);
    private final View view = mock(View.class);
    private final Piece piece = mock(Piece.class);
    private final HashMap<Position, Piece> board = HashMap.of(A1, piece);
    private final GameState state = spy(new GameState(board, White, HashSet.empty()));
    private final Move move = mock(Move.class);
    private Game game;

    @BeforeEach
    void setUp() {
        game = spy(new Game(white, black, view));
        when(game.initialState()).thenReturn(state);
        when(state.advance(any())).thenReturn(state);
    }

    @Test
    void playOneRound() {
        when(game.isOver(state)).thenReturn(false).thenReturn(true);
        when(white.takeInput()).thenReturn(A1).thenReturn(B3);
        when(piece.getPlayer()).thenReturn(White);
        when(piece.computePossibleMoves(A1)).thenReturn(Stream.of(move));
        when(move.validate(any())).thenReturn(true);
        when(move.getTo()).thenReturn(B3);
//        when(move.) TODO when the gap is bridged, stub the move execution
        game.play();

        verify(game, times(2)).isOver(state);
        verify(white, times(2)).takeInput();
        verify(view, times(3)).refresh(eq(board), any(), eq(HashSet.empty()), any(), any());
        verify(view).announce("White moves: A1 - B3");
        verify(piece).getPlayer();
        verify(piece).computePossibleMoves(A1);
        verify(move).validate(any()); // TODO when the gap is bridged, pass map
        verify(move).getTo();
        verify(move).execute(any()); // TODO when the gap is bridged, pass map
        verify(state).advance(board);
    }

    @Test
    void playOneRoundWithBogusInputs() {
        when(game.isOver(state)).thenReturn(false).thenReturn(true);
        when(white.takeInput()).thenReturn(A3).thenReturn(A1).thenReturn(G7).thenReturn(B3);
        when(piece.getPlayer()).thenReturn(White);
        when(piece.computePossibleMoves(A1)).thenReturn(Stream.of(move));
        when(move.validate(any())).thenReturn(true);
        when(move.getTo()).thenReturn(B3);
//        when(move.) TODO when the gap is bridged, stub the move execution
        game.play();

        verify(game, times(2)).isOver(state);
        verify(white, times(4)).takeInput();
        verify(view, times(3)).refresh(eq(board), any(), eq(HashSet.empty()), any(), any());
        verify(view).announce("Invalid pickup: A3");
        verify(view).announce("Invalid move: A1 - G7");
        verify(view).announce("White moves: A1 - B3");
        verify(piece).getPlayer();
        verify(piece).computePossibleMoves(A1);
        verify(move).validate(any()); // TODO when the gap is bridged, pass map
        verify(move).getTo();
        verify(move).execute(any()); // TODO when the gap is bridged, pass map
        verify(state).advance(board);
    }

    // TODO Game deserves, like, a couple dozen tests

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(white, black, view, piece, move);
    }
}