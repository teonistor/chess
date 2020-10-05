package io.github.teonistor.chess.board;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class InitialBoardProviderTest {

    @Test
    void initialSetup() {
        // TODO In progress
        assertThat(new InitialBoardProvider().createInitialBoard()).hasSize(32);
    }
}