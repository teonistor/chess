package io.github.teonistor.chess.board;

import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.PieceBox;
import io.github.teonistor.chess.rule.UnderAttackRule;
import io.vavr.collection.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.github.teonistor.chess.core.Player.Black;
import static io.github.teonistor.chess.core.Player.White;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

// Also covers PieceBox
// Tests are a sequence
class InitialBoardProviderTest {

    private static UnderAttackRule rule;
    private static Map<Position, Piece> board;

    @BeforeAll
    static void setup() {
        rule = mock(UnderAttackRule.class);
        board = new InitialBoardProvider(new PieceBox(rule)).createInitialBoard();
    }

    @Test
    void thereAre32Pieces() {
        assertThat(board).hasSize(32);
    }

    @Test
    void pieceTypes() {
        assertThat(classNameAt(Position.A1)).isEqualTo("Rook");
        assertThat(classNameAt(Position.B1)).isEqualTo("Knight");
        assertThat(classNameAt(Position.C1)).isEqualTo("Bishop");
        assertThat(classNameAt(Position.D1)).isEqualTo("Queen");
        assertThat(classNameAt(Position.E1)).isEqualTo("King");
        assertThat(classNameAt(Position.F1)).isEqualTo("Bishop");
        assertThat(classNameAt(Position.G1)).isEqualTo("Knight");
        assertThat(classNameAt(Position.H1)).isEqualTo("Rook");
        assertThat(classNameAt(Position.A2)).isEqualTo("Pawn");
        assertThat(classNameAt(Position.B2)).isEqualTo("Pawn");
        assertThat(classNameAt(Position.C2)).isEqualTo("Pawn");
        assertThat(classNameAt(Position.D2)).isEqualTo("Pawn");
        assertThat(classNameAt(Position.E2)).isEqualTo("Pawn");
        assertThat(classNameAt(Position.F2)).isEqualTo("Pawn");
        assertThat(classNameAt(Position.G2)).isEqualTo("Pawn");
        assertThat(classNameAt(Position.H2)).isEqualTo("Pawn");
        assertThat(classNameAt(Position.A7)).isEqualTo("Pawn");
        assertThat(classNameAt(Position.B7)).isEqualTo("Pawn");
        assertThat(classNameAt(Position.C7)).isEqualTo("Pawn");
        assertThat(classNameAt(Position.D7)).isEqualTo("Pawn");
        assertThat(classNameAt(Position.E7)).isEqualTo("Pawn");
        assertThat(classNameAt(Position.F7)).isEqualTo("Pawn");
        assertThat(classNameAt(Position.G7)).isEqualTo("Pawn");
        assertThat(classNameAt(Position.H7)).isEqualTo("Pawn");
        assertThat(classNameAt(Position.A8)).isEqualTo("Rook");
        assertThat(classNameAt(Position.B8)).isEqualTo("Knight");
        assertThat(classNameAt(Position.C8)).isEqualTo("Bishop");
        assertThat(classNameAt(Position.D8)).isEqualTo("Queen");
        assertThat(classNameAt(Position.E8)).isEqualTo("King");
        assertThat(classNameAt(Position.F8)).isEqualTo("Bishop");
        assertThat(classNameAt(Position.G8)).isEqualTo("Knight");
        assertThat(classNameAt(Position.H8)).isEqualTo("Rook");
    }

    private String classNameAt(Position a2) {
        return board.get(a2).get().getClass().getSimpleName();
    }

    @Test
    void players() {
        assertThat(playerAt(Position.A1)).isEqualTo(White);
        assertThat(playerAt(Position.B1)).isEqualTo(White);
        assertThat(playerAt(Position.C1)).isEqualTo(White);
        assertThat(playerAt(Position.D1)).isEqualTo(White);
        assertThat(playerAt(Position.E1)).isEqualTo(White);
        assertThat(playerAt(Position.F1)).isEqualTo(White);
        assertThat(playerAt(Position.G1)).isEqualTo(White);
        assertThat(playerAt(Position.H1)).isEqualTo(White);
        assertThat(playerAt(Position.A2)).isEqualTo(White);
        assertThat(playerAt(Position.B2)).isEqualTo(White);
        assertThat(playerAt(Position.C2)).isEqualTo(White);
        assertThat(playerAt(Position.D2)).isEqualTo(White);
        assertThat(playerAt(Position.E2)).isEqualTo(White);
        assertThat(playerAt(Position.F2)).isEqualTo(White);
        assertThat(playerAt(Position.G2)).isEqualTo(White);
        assertThat(playerAt(Position.H2)).isEqualTo(White);
        assertThat(playerAt(Position.A7)).isEqualTo(Black);
        assertThat(playerAt(Position.B7)).isEqualTo(Black);
        assertThat(playerAt(Position.C7)).isEqualTo(Black);
        assertThat(playerAt(Position.D7)).isEqualTo(Black);
        assertThat(playerAt(Position.E7)).isEqualTo(Black);
        assertThat(playerAt(Position.F7)).isEqualTo(Black);
        assertThat(playerAt(Position.G7)).isEqualTo(Black);
        assertThat(playerAt(Position.H7)).isEqualTo(Black);
        assertThat(playerAt(Position.A8)).isEqualTo(Black);
        assertThat(playerAt(Position.B8)).isEqualTo(Black);
        assertThat(playerAt(Position.C8)).isEqualTo(Black);
        assertThat(playerAt(Position.D8)).isEqualTo(Black);
        assertThat(playerAt(Position.E8)).isEqualTo(Black);
        assertThat(playerAt(Position.F8)).isEqualTo(Black);
        assertThat(playerAt(Position.G8)).isEqualTo(Black);
        assertThat(playerAt(Position.H8)).isEqualTo(Black);
    }

    private Player playerAt(Position h1) {
        return board.get(h1).get().getPlayer();
    }

    @Test
    void kingsHaveTheRule() {
        assertThat(board.get(Position.E1).get()).hasFieldOrPropertyWithValue("underAttackRule", rule);
        assertThat(board.get(Position.E8).get()).hasFieldOrPropertyWithValue("underAttackRule", rule);
    }
}