package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.core.UnderAttackRule;
import io.github.teonistor.chess.piece.Knight;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.Rook;
import io.github.teonistor.chess.piece.King;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


class CastleTest extends MoveTest{

    private final UnderAttackRule rule = mock(UnderAttackRule.class);

    @ParameterizedTest
    @CsvSource({"E1,G1,White,H1,E1",
                "E1,G1,White,H1,F1",
                "E1,C1,White,A1,E1",
                "E1,C1,White,A1,D1",
                "E8,G8,Black,H8,E8",
                "E8,G8,Black,H8,F8",
                "E8,G8,Black,H8,G8",
                "E8,C8,Black,A8,E8",
                "E8,C8,Black,A8,D8",
                "E8,C8,Black,A8,C8"})
    void validateInvalidBecauseUnderAttack(final Position from, final Position to, final Player player, final Position rookPos, final Position underAttack) {
        final HashMap<Position, Piece> board = HashMap.of(rookPos, new Rook(player));
        when(rule.checkAttack(board, underAttack, player)).thenReturn(true);

        assertThat(new Castle(from, to, rule).validate(stateWith(board))).isFalse();
        verify(rule, atMost(2)).checkAttack(eq(board), any(), eq(player));
    }

    @ParameterizedTest
    @CsvSource({"E1,G1,White", "E1,C1,White",
                "E8,G8,Black", "E8,C8,Black"})
    void validateInvalidBecauseNoRook(final Position from, final Position to, final Player player) {
        final HashMap<Position, Piece> board = HashMap.empty();

        assertThat(new Castle(from, to, rule).validate(stateWith(board))).isFalse();
        verify(rule, atMost(2)).checkAttack(eq(board), any(), eq(player));
    }

    @ParameterizedTest
    @CsvSource({"E1,G1,White,H1", "E1,C1,White,A1",
                "E8,G8,Black,H8", "E8,C8,Black,A8"})
    void validateInvalidBecauseRookHadMoved(final Position from, final Position to, final Player player, final Position rookPos) {
        final HashMap<Position, Piece> board = HashMap.of(rookPos, new Rook(player));
        final GameState state = new GameState(board, player, List.empty(), stateWith(HashMap.empty(), player.next()));

        assertThat(new Castle(from, to, rule).validate(state)).isFalse();
        verify(rule, atMost(2)).checkAttack(eq(board), any(), eq(player));
    }

    @ParameterizedTest
    @CsvSource({"E1,G1,White,H1,F1",
                "E1,G1,White,H1,G1",
                "E1,C1,White,A1,D1",
                "E1,C1,White,A1,C1",
                "E1,C1,White,A1,B1",
                "E8,G8,Black,H8,F8",
                "E8,G8,Black,H8,G8",
                "E8,C8,Black,A8,D8",
                "E8,C8,Black,A8,C8",
                "E8,C8,Black,A8,B8"})
    void validateInvalidBecauseOccupied(final Position from, final Position to, final Player player, final Position rookPos, final Position occupied) {
        final HashMap<Position, Piece> board = HashMap.of(rookPos, new Rook(player), occupied, new Knight(player));

        assertThat(new Castle(from, to, rule).validate(stateWith(board))).isFalse();
        verify(rule, atMost(2)).checkAttack(eq(board), any(), eq(player));
    }

    @ParameterizedTest
    @CsvSource({"E1,G1,White,H1", "E1,C1,White,A1",
                "E8,G8,Black,H8", "E8,C8,Black,A8"})
    void validateValid(final Position from, final Position to, final Player player, final Position rookPos) {
        final HashMap<Position, Piece> board = HashMap.of(from, new King(player, rule), rookPos, new Rook(player));

        assertThat(new Castle(from, to, rule).validate(stateWith(board, player))).isTrue();
        verify(rule, times(2)).checkAttack(eq(board), any(), eq(player));
    }

    @ParameterizedTest
    @CsvSource({"E1,G1,White,H1,F1", "E1,C1,White,A1,D1",
                "E8,G8,Black,H8,F8", "E8,C8,Black,A8,D8"})
    void execute(final Position from, final Position to, final Player player, final Position rookFrom, final Position rookTo) {
        final King king = new King(player, rule);
        final Rook rook = new Rook(player);
        final HashMap<Position, Piece> board = HashMap.of(from, king, rookFrom, rook);

        final Map<Position,Piece> output = new Castle(from, to, rule).executeSingleOutcome(stateWith(board)).getBoard();

        assertThat(output.get(to)).containsExactly(king);
        assertThat(output.get(rookTo)).containsExactly(rook);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(rule);
    }
}