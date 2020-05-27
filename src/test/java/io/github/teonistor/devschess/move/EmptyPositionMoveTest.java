package io.github.teonistor.devschess.move;

import io.github.teonistor.devschess.board.Position;
import io.github.teonistor.devschess.Player;
import io.github.teonistor.devschess.piece.Piece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;

import java.util.EnumMap;

import static io.github.teonistor.devschess.board.Position.A1;
import static io.github.teonistor.devschess.board.Position.C6;
import static io.github.teonistor.devschess.board.Position.G1;
import static io.github.teonistor.devschess.board.Position.H6;
import static io.github.teonistor.devschess.Player.Black;
import static io.github.teonistor.devschess.Player.White;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


class EmptyPositionMoveTest {

    private @Mock Piece moving;
    private @Mock Piece obstacle;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void validateValid() {
        final EnumMap<Position,Piece> board = new EnumMap<>(Position.class);
        board.put(A1, moving);

        assertThat(new EmptyPositionMove(A1,C6,Black).validate(board)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(Player.class)
    void validateInvalid(final Player player) {
        final EnumMap<Position,Piece> board = new EnumMap<>(Position.class);
        board.put(A1, moving);
        board.put(C6, obstacle);
        when(obstacle.getPlayer()).thenReturn(player);

        // One player stays fixed to prove that the move cannot be done irrespective of who owns the obstacle
        assertThat(new EmptyPositionMove(A1,C6,White).validate(board)).isFalse();
    }

    @Test
    void execute() {
        final EnumMap<Position,Piece> board = new EnumMap<>(Position.class);
        board.put(H6, moving);

        new EmptyPositionMove(H6,G1,Black).execute(board);

        assertThat(board).hasSize(1);
        assertThat(board).containsEntry(G1, moving);
    }
}