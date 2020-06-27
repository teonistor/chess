package io.github.teonistor.chess.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class CheckRuleTest {

    private final UnderAttackRule underAttackRule = mock(UnderAttackRule.class);

    @Test
    void validateMove() {
        fail("TODO");
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(underAttackRule);
    }
}
