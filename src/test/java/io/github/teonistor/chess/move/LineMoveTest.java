package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static io.github.teonistor.chess.board.Position.B3;
import static io.github.teonistor.chess.board.Position.C3;
import static io.github.teonistor.chess.board.Position.D2;
import static io.github.teonistor.chess.board.Position.D3;
import static io.github.teonistor.chess.board.Position.D4;
import static io.github.teonistor.chess.board.Position.E3;
import static io.github.teonistor.chess.board.Position.F3;
import static org.assertj.core.api.Assertions.assertThat;

@MockitoSettings
class LineMoveTest extends MoveTest {

    private @Mock Piece moving;
    private @Mock Piece obstacle;

    @ParameterizedTest
    @EnumSource(Player.class)
    void validateValid(final Player player) {
        final Map<Position,Piece> board = HashMap.of(B3, moving, D2, obstacle, D4, obstacle);

        assertThat(new LineMove(B3, F3, List.of(C3, D3, E3)).validate(stateWith(board))).isTrue();
    }

    @ParameterizedTest
    @EnumSource(Player.class)
    void validateInvalid(final Player player) {
        final Map<Position,Piece> board = HashMap.of(B3, moving, D3, obstacle);

        assertThat(new LineMove(B3, F3, List.of(C3, D3, E3)).validate(stateWith(board))).isFalse();
    }
}