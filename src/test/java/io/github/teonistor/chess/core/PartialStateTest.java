package io.github.teonistor.chess.core;

import io.github.teonistor.chess.piece.Piece;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static io.github.teonistor.chess.core.PartialState.NIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@MockitoSettings
class PartialStateTest {

    @Test
    void completeState() {
        assertThat(NIL.completeState()).isEmpty();
    }

    @Test
    void triggerViewIfNeeded() {
        assertThat(NIL.triggerViewIfNeeded()).isEmpty();
    }

    @Test
    void withPromotionInput(final @Mock Piece piece) {
        assertThatThrownBy(() -> NIL.withPromotionInput(piece)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void getBoard() {
        assertThatThrownBy(() -> NIL.getBoard()).isInstanceOf(UnsupportedOperationException.class);
    }
}