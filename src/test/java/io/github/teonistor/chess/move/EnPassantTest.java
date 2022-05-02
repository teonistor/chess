package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.PieceBox;
import io.github.teonistor.chess.rule.UnderAttackRule;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static io.github.teonistor.chess.board.Position.A5;
import static io.github.teonistor.chess.board.Position.A6;
import static io.github.teonistor.chess.board.Position.A7;
import static io.github.teonistor.chess.board.Position.B5;
import static io.github.teonistor.chess.board.Position.C5;
import static io.github.teonistor.chess.board.Position.C6;
import static io.github.teonistor.chess.board.Position.C7;
import static io.github.teonistor.chess.board.Position.D2;
import static io.github.teonistor.chess.board.Position.D3;
import static io.github.teonistor.chess.board.Position.D4;
import static io.github.teonistor.chess.board.Position.E4;
import static io.github.teonistor.chess.board.Position.F2;
import static io.github.teonistor.chess.board.Position.F3;
import static io.github.teonistor.chess.board.Position.F4;
import static io.github.teonistor.chess.board.Position.G4;
import static io.github.teonistor.chess.board.Position.H2;
import static io.github.teonistor.chess.board.Position.H3;
import static io.github.teonistor.chess.board.Position.H4;
import static io.github.teonistor.chess.core.Player.Black;
import static io.github.teonistor.chess.core.Player.White;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@MockitoSettings
class EnPassantTest extends MoveTest{
    private static final PieceBox box = new PieceBox(mock(UnderAttackRule.class));

    private @Mock Piece moving;
    private @Mock Piece victim;

    @Test
    void validateValid() {
        assertThat(new EnPassant(G4, F3, F4, F2).validate(new GameState(HashMap.of(F4, box.whitePawn), Black, List.empty(),
                stateWith(HashMap.of(F2, box.whitePawn), Black))))
                .isTrue();
    }

    @Test
    void validateInvalid() {
        // Nothing there
        assertThat(new EnPassant(G4, F3, F4, F2).validate(stateWith(HashMap.empty()))).isFalse();

        //It wasn't where it should have been before
        assertThat(new EnPassant(G4, H3, H4, H2).validate(new GameState(HashMap.of(H4, box.whitePawn), Black, List.empty(),
                stateWith(HashMap.of(H3, box.whitePawn), Black)))).isFalse();

        //It's not where it should be now
        assertThat(new EnPassant(E4, D3, D4, D2).validate(new GameState(HashMap.of(D3, box.whitePawn), Black, List.empty(),
                stateWith(HashMap.of(D2, box.whitePawn), Black)))).isFalse();

        // Wrong color (though the sequence doesn't really make sense
        assertThat(new EnPassant(G4, F3, F4, F2).validate(new GameState(HashMap.of(F4, box.blackPawn), Black, List.empty(),
                stateWith(HashMap.of(F2, box.blackPawn), Black)))).isFalse();

        // Two pawns not one
        final HashMap<Position, Piece> board = HashMap.of(C5, box.blackPawn, C7, box.blackPawn);
        assertThat(new EnPassant(B5, C6, C5, C7).validate(new GameState(board, White, List.empty(),
                stateWith(board, Black)))).isFalse();
    }

    @Test
    void execute() {
        final HashMap<Position, Piece> board = HashMap.of(B5, moving, A5, victim);
        final HashMap<Position, Piece> before = HashMap.of(B5, moving, A7, victim);

        final GameState state = new EnPassant(B5, A6, A5, A7).executeSingleOutcome(new GameState(board, White, List.empty(), stateWith(before, Black)));

        assertThat(state.getBoard()).containsExactly(new Tuple2<>(A6, moving));
        assertThat(state.getPlayer()).isEqualTo(Black);
        assertThat(state.getCapturedPieces()).containsExactly(victim);
    }
}