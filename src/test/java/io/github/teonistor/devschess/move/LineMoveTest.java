package io.github.teonistor.devschess.move;

import io.github.teonistor.devschess.Player;
import io.github.teonistor.devschess.board.Position;
import io.github.teonistor.devschess.piece.Piece;
import io.vavr.collection.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;

import java.util.EnumMap;

import static io.github.teonistor.devschess.board.Position.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


class LineMoveTest {

    private @Mock Piece moving;
    private @Mock Piece obstacle;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @ParameterizedTest
    @EnumSource(Player.class)
    void validateValid(final Player player) {
        final EnumMap<Position,Piece> board = new EnumMap<>(Position.class);
        board.put(B3, moving);
        board.put(D2, obstacle);
        board.put(D4, obstacle);
        when(obstacle.getPlayer()).thenReturn(player);

        assertThat(new LineMove(B3, F3, player, List.of(C3, D3, E3)).validate(board)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(Player.class)
    void validateInvalid(final Player player) {
        final EnumMap<Position,Piece> board = new EnumMap<>(Position.class);
        board.put(B3, moving);
        board.put(D3, obstacle);
        when(obstacle.getPlayer()).thenReturn(player);

        assertThat(new LineMove(B3, F3, player, List.of(C3, D3, E3)).validate(board)).isFalse();
    }
}