package io.github.teonistor.devschess;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static io.github.teonistor.devschess.Board.Position.*;
import static org.assertj.core.api.Assertions.assertThat;

class BoardTest {
    private final SoftAssertions assertj = new SoftAssertions();

    @Test
    void aFewSimpleSteps() {
        assertj.assertThat(A2.right()).isEqualTo(A3);
        assertj.assertThat(C1.left()).isEqualTo(OutOfBoard);
        assertj.assertThat(B7.up()).isEqualTo(C7);
        assertj.assertThat(H5.down()).isEqualTo(G5);
        assertj.assertThat(OutOfBoard.left()).isEqualTo(OutOfBoard);
        assertj.assertThat(F8.right()).isEqualTo(OutOfBoard);
        assertj.assertAll();
    }

    @Test
    void aFewComplicatedSteps() {
        assertj.assertThat(A2.right().up().up().right()).isEqualTo(C4);
        assertj.assertThat(H3.right().down().left().down()).isEqualTo(F3);
        assertj.assertThat(D2.right().right().left().left()).isEqualTo(D2);
        assertj.assertThat(E7.right().right().left().left()).isEqualTo(OutOfBoard);
        assertj.assertAll();
    }

    @Test
    void initialSetupPartial() {
        assertThat(Board.initialSetup()).hasSize(32);
    }
}