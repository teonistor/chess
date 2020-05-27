package io.github.teonistor.devschess.move;

import io.github.teonistor.devschess.board.Position;
import io.github.teonistor.devschess.piece.Piece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import java.util.EnumMap;
import static io.github.teonistor.devschess.board.Position.A1;
import static io.github.teonistor.devschess.board.Position.B5;
import static io.github.teonistor.devschess.board.Position.C6;
import static io.github.teonistor.devschess.board.Position.E2;
import static io.github.teonistor.devschess.Player.Black;
import static io.github.teonistor.devschess.Player.White;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


class CapturingMoveTest {

    private @Mock Piece moving;
    private @Mock Piece victim;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void validateValid() {
        final EnumMap<Position,Piece> board = new EnumMap<>(Position.class);
        board.put(A1, moving);
        board.put(C6, victim);
        when(victim.getPlayer()).thenReturn(Black);

        assertThat(new CapturingMove(A1,C6,White).validate(board)).isTrue();
    }

    @Test
    void validateInvalidNotOccupied() {
        final EnumMap<Position,Piece> board = new EnumMap<>(Position.class);
        board.put(A1, moving);

        assertThat(new CapturingMove(A1,C6,White).validate(board)).isFalse();
    }

    @Test
    void validateInvalidNotOpponent() {
        final EnumMap<Position,Piece> board = new EnumMap<>(Position.class);
        board.put(A1, moving);
        board.put(C6, victim);
        when(victim.getPlayer()).thenReturn(White);

        assertThat(new CapturingMove(A1,C6,White).validate(board)).isFalse();
    }

    @Test
    void execute() {
        final EnumMap<Position,Piece> board = new EnumMap<>(Position.class);
        board.put(B5, moving);
        board.put(E2, victim);

        new CapturingMove(B5,E2,Black).execute(board);

        assertThat(board).hasSize(1);
        assertThat(board).containsEntry(E2, moving);
    }
}