package io.github.teonistor.devschess;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static io.github.teonistor.devschess.Board.Position.*;
import static org.assertj.core.api.Assertions.assertThat;

class BoardTest {
    private final SoftAssertions assertj = new SoftAssertions();

    @Test
    void aFewSimpleSteps() {
        assertj.assertThat(A2.right()).isEqualTo(B2);
        assertj.assertThat(C1.down()).isEqualTo(OutOfBoard);
        assertj.assertThat(B7.up()).isEqualTo(B8);
        assertj.assertThat(H5.down()).isEqualTo(H4);
        assertj.assertThat(OutOfBoard.left()).isEqualTo(OutOfBoard);
        assertj.assertThat(F8.up()).isEqualTo(OutOfBoard);
        assertj.assertAll();
    }

    @Test
    void aFewComplicatedSteps() {
        assertj.assertThat(A2.right().up().up().right()).isEqualTo(C4);
        assertj.assertThat(E3.right().down().left().down()).isEqualTo(E1);
        assertj.assertThat(D2.right().right().left().left()).isEqualTo(D2);
        assertj.assertThat(G7.right().right().left().left()).isEqualTo(OutOfBoard);
        assertj.assertAll();
    }

    @Test
    void initialSetupPartial() {
        assertThat(Board.initialSetup()).hasSize(32);
    }
}