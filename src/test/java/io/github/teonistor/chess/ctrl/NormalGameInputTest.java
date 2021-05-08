package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@MockitoSettings
class NormalGameInputTest implements RandomPositionsTestMixin {

    private @Mock Game game;
    private @Mock Game newGame;

    @Test
    void execute() {
        final Position from = randomPositions.next();
        final Position to = randomPositions.next();
        when(game.processInput(from, to)).thenReturn(newGame);
        assertThat(new NormalGameInput(from, to).execute(game)).isEqualTo(newGame);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(game, newGame);
    }
}