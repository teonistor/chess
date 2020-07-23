package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.core.UnderAttackRule;
import io.github.teonistor.chess.piece.King;
import io.github.teonistor.chess.piece.Knight;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.Rook;
import io.vavr.collection.HashMap;
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
                "E1,G1,White,H1,G1",
                "E1,C1,White,A1,E1",
                "E1,C1,White,A1,D1",
                "E1,C1,White,A1,C1",
                "E8,G8,Black,H8,E8",
                "E8,G8,Black,H8,F8",
                "E8,G8,Black,H8,G8",
                "E8,C8,Black,A8,E8",
                "E8,C8,Black,A8,D8",
                "E8,C8,Black,A8,C8"})
    void validateInvalidBecauseUnderAttack(Position from, Position to, Player player, Position rookPos, Position underAttack) {
        HashMap<Position, Piece> board = HashMap.of(rookPos, new Rook(player));
        when(rule.checkAttack(board, underAttack, player)).thenReturn(true);

        assertThat(new Castle(from, to, player, rule).validate(stateWith(board))).isFalse();
        verify(rule, atMost(3)).checkAttack(eq(board), any(), eq(player));
    }

    @ParameterizedTest
    @CsvSource({"E1,G1,White", "E1,C1,White",
                "E8,G8,Black", "E8,C8,Black"})
    void validateInvalidBecauseNoRook(Position from, Position to, Player player) {
        HashMap<Position, Piece> board = HashMap.empty();

        assertThat(new Castle(from, to, player, rule).validate(stateWith(board))).isFalse();
        verify(rule, atMost(3)).checkAttack(eq(board), any(), eq(player));
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
    void validateInvalidBecauseOccupied(Position from, Position to, Player player, Position rookPos, Position occupied) {
        HashMap<Position, Piece> board = HashMap.of(rookPos, new Rook(player), occupied, new Knight(player));

        assertThat(new Castle(from, to, player, rule).validate(stateWith(board))).isFalse();
        verify(rule, atMost(3)).checkAttack(eq(board), any(), eq(player));
    }

    @ParameterizedTest
    @CsvSource({"E1,G1,White,H1", "E1,C1,White,A1",
                "E8,G8,Black,H8", "E8,C8,Black,A8"})
    void validateValid(Position from, Position to, Player player, Position rookPos) {
        HashMap<Position, Piece> board = HashMap.of(rookPos, new Rook(player));

        assertThat(new Castle(from, to, player, rule).validate(stateWith(board))).isTrue();
        verify(rule, times(3)).checkAttack(eq(board), any(), eq(player));
    }

    @ParameterizedTest
    @CsvSource({"E1,G1,White,H1,F1", "E1,C1,White,A1,D1",
                "E8,G8,Black,H8,F8", "E8,C8,Black,A8,D8"})
    void execute(Position from, Position to, Player player, Position rookFrom, Position rookTo) {
        HashMap<Position, Piece> board = HashMap.of(from, new King(player), rookFrom, new Rook(player));

        Map<Position, Piece> output = new Castle(from, to, player, rule).execute(board, nonCapturingReturnBoard, captureNotExpected);

        assertThat(output.get(to).get()).isExactlyInstanceOf(King.class);
        assertThat(output.get(to).get().getPlayer()).isEqualTo(player);
        assertThat(output.get(rookTo).get()).isExactlyInstanceOf(Rook.class);
        assertThat(output.get(rookTo).get().getPlayer()).isEqualTo(player);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(rule);
    }
}