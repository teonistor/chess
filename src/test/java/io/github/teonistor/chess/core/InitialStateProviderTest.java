package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.InitialBoardProvider;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import static io.github.teonistor.chess.core.Player.White;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class InitialStateProviderTest {

    @Test
    void createInitialState() {
        final InitialBoardProvider provider = mock(InitialBoardProvider.class);
        setField(World.class, "initialBoardProvider", provider);
        when(provider.createInitialBoard()).thenReturn(HashMap.empty());

        final GameState state = new InitialStateProvider().createState();

        assertThat(state).hasFieldOrPropertyWithValue("board", HashMap.empty())
                         .hasFieldOrPropertyWithValue("player", White)
                         .hasFieldOrPropertyWithValue("capturedPieces", List.empty());
        verify(provider).createInitialBoard();
        verifyNoMoreInteractions(provider);
    }
}
