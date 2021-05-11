package io.github.teonistor.chess.move;

import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.GameStateKey;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@MockitoSettings
class SingleOutcomeMoveTest {

    @Mock private GameStateKey key;
    @Mock private GameState state1;
    @Mock private GameState state2;
    @Mock private SingleOutcomeMove move;

    @BeforeEach
    void setUp() {
    }

    @Test
    void execute() {
        // Class under test is a mixin-ish template method pattern in an abstract-method-heavy inheritance tree so it's much easier to test through a mock calling one real method
        when(move.executeSingleOutcome(state1)).thenReturn(state2);
        when(move.execute(state1)).thenCallRealMethod();

        val result = move.execute(state1);

        assertThat(result).hasSize(1);
        assertThat(result.keySet().get().apply(key)).isEqualTo(key);
        assertThat(result.values().get()).isEqualTo(state2);
    }

    @AfterEach
    void tearDown() {
        Mockito.verifyNoMoreInteractions(key,state1,state2,move);
    }
}