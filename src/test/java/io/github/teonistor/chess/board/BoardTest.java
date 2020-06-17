package io.github.teonistor.chess.board;

import io.github.teonistor.chess.core.UnderAttackRule;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class BoardTest {

    @Test
    void initialSetup() {
        // TODO In progress
        assertThat(new Board(mock(UnderAttackRule.class)).initialSetup()).hasSize(32);
    }
}