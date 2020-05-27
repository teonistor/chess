package io.github.teonistor.devschess.move;

import io.github.teonistor.devschess.board.Position;
import io.github.teonistor.devschess.piece.Piece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import java.util.EnumMap;
import static io.github.teonistor.devschess.board.Position.*;
import static io.github.teonistor.devschess.Player.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


class CaptureIndependentMoveTest {

    private @Mock Piece moving;
    private @Mock Piece victim;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void validateCapture() {
        final EnumMap<Position,Piece> board = new EnumMap<>(Position.class);
        board.put(A1, moving);
        board.put(C6, victim);
        when(victim.getPlayer()).thenReturn(Black);

        assertThat(new CaptureIndependentMove(A1,C6,White).validate(board)).isTrue();
    }

    @Test
    void validateNonCapture() {
        final EnumMap<Position,Piece> board = new EnumMap<>(Position.class);
        board.put(A1, moving);

        assertThat(new CaptureIndependentMove(A1,C6,White).validate(board)).isTrue();
    }

    @Test
    void validateInvalid() {
        final EnumMap<Position,Piece> board = new EnumMap<>(Position.class);
        board.put(A1, moving);
        board.put(C6, victim);
        when(victim.getPlayer()).thenReturn(White);

        assertThat(new CaptureIndependentMove(A1,C6,White).validate(board)).isFalse();
    }

    @Test
    void executeCapture() {
        final EnumMap<Position,Piece> board = new EnumMap<>(Position.class);
        board.put(B5, moving);
        board.put(E2, victim);

        new CaptureIndependentMove(B5,E2,Black).execute(board);

        assertThat(board).hasSize(1);
        assertThat(board).containsEntry(E2, moving);
    }

    @Test
    void executeNonCapture() {
        final EnumMap<Position,Piece> board = new EnumMap<>(Position.class);
        board.put(B5, moving);

        new CaptureIndependentMove(B5,F1,Black).execute(board);

        assertThat(board).hasSize(1);
        assertThat(board).containsEntry(F1, moving);
    }
}