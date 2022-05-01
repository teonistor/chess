package io.github.teonistor.chess.rule;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.GameStateKey;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.PieceBox;
import io.github.teonistor.chess.piece.Rook;
import io.vavr.collection.HashMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static io.github.teonistor.chess.core.GameCondition.BlackWins;
import static io.github.teonistor.chess.core.GameCondition.Continue;
import static io.github.teonistor.chess.core.GameCondition.Stalemate;
import static io.github.teonistor.chess.core.GameCondition.WhiteWins;
import static io.github.teonistor.chess.core.Player.Black;
import static io.github.teonistor.chess.core.Player.White;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MockitoSettings
class GameOverCheckerTest {

    private final CheckRule rule = mock(CheckRule.class);
    private final PieceBox box = new PieceBox(mock(UnderAttackRule.class));

    @Test
    void constructorThrowsOnNull() {
        assertThatNullPointerException().isThrownBy(() -> new GameOverChecker(null))
                .withMessageContaining("checkRule");
    }

    @Test
    void blackWins() {
        final HashMap<Position, Piece> board = HashMap.of(Position.A1, box.whiteKing, Position.H8, box.blackKing, Position.D4, box.whiteRook);
        when(rule.check(board, White)).thenReturn(true);

        assertThat(new GameOverChecker(rule).check(board, White, HashMap.empty())).isEqualTo(BlackWins);
    }

    @Test
    void whiteWins() {
        final HashMap<Position, Piece> board = HashMap.of(Position.A1, box.whiteKing, Position.H8, box.blackKing, Position.F3, box.blackRook);
        when(rule.check(board, Black)).thenReturn(true);

        assertThat(new GameOverChecker(rule).check(board, Black, HashMap.empty())).isEqualTo(WhiteWins);
    }

    @ParameterizedTest
    @EnumSource(Player.class)
    void stalemateByNoMovesLeft(final Player player) {
        final HashMap<Position, Piece> board = HashMap.of(Position.A1, box.whiteKing, Position.H8, box.blackKing);
        assertThat(new GameOverChecker(rule).check(board, player, HashMap.empty())).isEqualTo(Stalemate);
    }

    @ParameterizedTest
    @EnumSource(Player.class)
    void stalemateByOnlyKingsLeft(final Player player, final @Mock GameStateKey key) {
        final HashMap<Position, Piece> board = HashMap.of(Position.A1, box.whiteKing, Position.H8, box.blackKing);
        assertThat(new GameOverChecker(rule).check(board, player, HashMap.of(key, mock(GameState.class)))).isEqualTo(Stalemate);
    }

    @ParameterizedTest
    @EnumSource(Player.class)
    void gameContinues(final Player player, final @Mock GameStateKey key) {
        final HashMap<Position, Piece> board = HashMap.of(Position.A1, box.whiteKing, Position.H8, box.blackKing, Position.C6, new Rook(player));
        assertThat(new GameOverChecker(rule).check(board, player, HashMap.of(key, mock(GameState.class)))).isEqualTo(Continue);
    }
}
