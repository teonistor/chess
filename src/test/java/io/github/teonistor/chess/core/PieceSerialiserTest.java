package io.github.teonistor.chess.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.PieceBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class PieceSerialiserTest {

    private static PieceBox box;
    private static ObjectMapper om;

    @BeforeAll
    static void setUp() {
        box = new PieceBox(mock(UnderAttackRule.class));
        om = new ObjectMapper();
        om.registerModule(new PieceSerialiser(box));
    }

    @ParameterizedTest
    @MethodSource("pieceStringPairs")
    void serialise(Piece p, String s) throws JsonProcessingException {
        assertThat(om.writeValueAsString(p)).isEqualTo(s);
    }

    @ParameterizedTest
    @MethodSource("pieceStringPairs")
    void deserialise(Piece p, String s) throws JsonProcessingException {
        assertThat(om.readValue(s, Piece.class)).isEqualTo(p);
    }

    private static Object[][] pieceStringPairs() {
        return new Object[][]{
            {box.blackPawn, "\"BP\""},
            {box.blackRook, "\"BR\""},
            {box.blackKnight, "\"BN\""},
            {box.blackBishop, "\"BB\""},
            {box.blackQueen, "\"BQ\""},
            {box.blackKing, "\"BK\""},
            {box.whitePawn, "\"WP\""},
            {box.whiteRook, "\"WR\""},
            {box.whiteKnight, "\"WN\""},
            {box.whiteBishop, "\"WB\""},
            {box.whiteQueen, "\"WQ\""},
            {box.whiteKing, "\"WK\""}};
    }

    @Test
    void deserialiseJunk() throws JsonProcessingException {
        assertThatThrownBy(() -> om.readValue("\"AA\"", Piece.class)).isInstanceOf(InvalidFormatException.class)
                .hasMessageStartingWith("AA cannot be parsed as a piece");
    }
}