package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Board;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import org.junit.jupiter.api.Test;

import static io.github.teonistor.chess.core.Player.White;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class InitialStateProviderTest {

    @Test
    void createInitialState() {
        final Board board = mock(Board.class);
        when(board.initialSetup()).thenReturn(HashMap.empty());

        final GameState state = new InitialStateProvider(board).createInitialState();

        assertThat(state).hasFieldOrPropertyWithValue("board", HashMap.empty())
                         .hasFieldOrPropertyWithValue("player", White)
                         .hasFieldOrPropertyWithValue("capturedPieces", HashSet.empty());
        verify(board).initialSetup();
        verifyNoMoreInteractions(board);
    }
}
