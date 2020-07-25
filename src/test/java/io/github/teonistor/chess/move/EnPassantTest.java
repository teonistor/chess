package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static io.github.teonistor.chess.board.Position.F3;
import static io.github.teonistor.chess.board.Position.F4;
import static io.github.teonistor.chess.board.Position.G3;
import static io.github.teonistor.chess.board.Position.G4;
import static io.github.teonistor.chess.core.Player.Black;
import static io.github.teonistor.chess.core.Player.White;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

class EnPassantTest extends MoveTest{

    private @Mock Piece moving;
    private @Mock Piece victim;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void validateValid() {
        assertThat(new EnPassant(G4, F3, F4).validate(new GameState(HashMap.empty(), White, HashSet.empty(), F3))).isTrue();
    }

    @Test
    void validateInvalid() {
        assertThat(new EnPassant(F4, G3, G4).validate(stateWith(HashMap.empty()))).isFalse();
    }

    @Test
    void execute() {
        final HashMap<Position, Piece> board = HashMap.of(Position.E4, moving, Position.D4, victim);

        final GameState state = new EnPassant(Position.E4, Position.D3, Position.D4).execute(new GameState(board, Black, HashSet.empty(), Position.D3));

        assertThat(state.getBoard()).containsExactly(new Tuple2<>(Position.D3, moving));
        assertThat(state.getPlayer()).isEqualTo(White);
        assertThat(state.getCapturedPieces()).containsExactly(victim);
        assertThat(state.getPawnTrail()).isNull();
    }
}