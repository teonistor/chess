package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.PieceBox;
import io.github.teonistor.chess.piece.Rook;
import io.vavr.collection.HashMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.github.teonistor.chess.core.GameCondition.*;
import static io.github.teonistor.chess.core.Player.Black;
import static io.github.teonistor.chess.core.Player.White;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameOverCheckerTest {

    private final UnderAttackRule rule = mock(UnderAttackRule.class);
    private final PieceBox box = new PieceBox(rule);

    @Test
    void constructorThrowsOnNull() {
        assertThatNullPointerException().isThrownBy(() -> new GameOverChecker(null))
                .withMessageContaining("underAttackRule");
    }

    @Test
    void blackWins() {
        final HashMap<Position, Piece> board = HashMap.of(Position.A1, box.whiteKing, Position.H8, box.blackKing, Position.D4, box.whiteRook);
        when(rule.checkAttack(board, Position.A1, White)).thenReturn(true);

        assertThat(new GameOverChecker(rule).check(board, White, HashMap.of(Position.A1, HashMap.empty()))).isEqualTo(BlackWins);
    }

    @Test
    void whiteWins() {
        final HashMap<Position, Piece> board = HashMap.of(Position.A1, box.whiteKing, Position.H8, box.blackKing, Position.F3, box.blackRook);
        when(rule.checkAttack(board, Position.H8, Black)).thenReturn(true);

        assertThat(new GameOverChecker(rule).check(board, Black, HashMap.of(Position.H8, HashMap.empty()))).isEqualTo(WhiteWins);
    }

    @ParameterizedTest
    @CsvSource({"White,A1", "Black,H8"})
    void stalemateByNoMovesLeft(Player player, Position position) {
        final HashMap<Position, Piece> board = HashMap.of(Position.A1, box.whiteKing, Position.H8, box.blackKing);
        assertThat(new GameOverChecker(rule).check(board, player, HashMap.of(position, HashMap.empty()))).isEqualTo(Stalemate);
    }

    @ParameterizedTest
    @CsvSource({"White,A1", "Black,H8"})
    void stalemateByOnlyKingsLeft(Player player, Position position) {
        final HashMap<Position, Piece> board = HashMap.of(Position.A1, box.whiteKing, Position.H8, box.blackKing);
        assertThat(new GameOverChecker(rule).check(board, player, HashMap.of(position, HashMap.of(position, mock(GameState.class))))).isEqualTo(Stalemate);
    }

    @ParameterizedTest
    @CsvSource({"White,A1", "Black,H8"})
    void gameContinues(Player player, Position position) {
        final HashMap<Position, Piece> board = HashMap.of(Position.A1, box.whiteKing, Position.H8, box.blackKing, Position.C6, new Rook(player));
        assertThat(new GameOverChecker(rule).check(board, player, HashMap.of(position, HashMap.of(position, mock(GameState.class))))).isEqualTo(Continue);
    }
}
