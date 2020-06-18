package io.github.teonistor.chess.board;

import io.github.teonistor.chess.core.UnderAttackRule;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class InitialBoardProviderTest {

    @Test
    void initialSetup() {
        // TODO In progress
        assertThat(new InitialBoardProvider(mock(UnderAttackRule.class)).createInitialBoard()).hasSize(32);
    }
}