package io.github.teonistor.devschess.board;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class BoardTest {

    @Test
    void initialSetup() {
        // In progress
        assertThat(Board.initialSetup()).hasSize(32);
    }
}