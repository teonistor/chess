package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.piece.Pawn;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static io.github.teonistor.chess.board.Position.*;
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
        assertThat(new EnPassant(G4, F3, F4, F2).validate(new GameState(HashMap.of(F4, new Pawn(White)), Black, HashSet.empty(),
                stateWith(HashMap.of(F2, new Pawn(White)), Black), null)))
                .isTrue();
    }

    @Test
    void validateInvalid() {
        // Nothing there
        assertThat(new EnPassant(G4, F3, F4, F2).validate(stateWith(HashMap.empty()))).isFalse();

        //It wasn't where it should have been before
        assertThat(new EnPassant(G4, H3, H4, H2).validate(new GameState(HashMap.of(H4, new Pawn(White)), Black, HashSet.empty(),
                stateWith(HashMap.of(H3, new Pawn(White)), Black), null))).isFalse();

        //It's not where it should be now
        assertThat(new EnPassant(E4, D3, D4, D2).validate(new GameState(HashMap.of(D3, new Pawn(White)), Black, HashSet.empty(),
                stateWith(HashMap.of(D2, new Pawn(White)), Black), null))).isFalse();

        // Wrong color (though the sequence doesn't really make sense
        assertThat(new EnPassant(G4, F3, F4, F2).validate(new GameState(HashMap.of(F4, new Pawn(Black)), Black, HashSet.empty(),
                stateWith(HashMap.of(F2, new Pawn(Black)), Black), null))).isFalse();

        // Two pawns not one
        final HashMap<Position, Piece> board = HashMap.of(C5, new Pawn(Black), C7, new Pawn(Black));
        assertThat(new EnPassant(B5, C6, C5, C7).validate(new GameState(board, White, HashSet.empty(),
                stateWith(board, Black), null))).isFalse();
    }

    @Test
    void execute() {
        final HashMap<Position, Piece> board = HashMap.of(B5, moving, A5, victim);
        final HashMap<Position, Piece> before = HashMap.of(B5, moving, A7, victim);

        final GameState state = new EnPassant(B5, A6, A5, A7).execute(new GameState(board, White, HashSet.empty(), stateWith(before, Black), null));

        assertThat(state.getBoard()).containsExactly(new Tuple2<>(A6, moving));
        assertThat(state.getPlayer()).isEqualTo(Black);
        assertThat(state.getCapturedPieces()).containsExactly(victim);
    }
}