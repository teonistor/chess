package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.piece.Piece;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@MockitoSettings
class PromotionGameInputTest {

    private @Mock Piece piece;
    private @Mock Game game;
    private @Mock Game newGame;

    @Test
    void execute() {
        when(game.processInput(piece)).thenReturn(newGame);
        assertThat(new PromotionGameInput(piece).execute(game)).isEqualTo(newGame);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(piece, game, newGame);
    }
}