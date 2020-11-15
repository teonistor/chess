package io.github.teonistor.chess.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.teonistor.chess.piece.King;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.PieceBox;
import io.github.teonistor.chess.piece.Rook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.github.teonistor.chess.core.Player.White;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PieceSerialiserTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void serialise() {
        final PieceBox box = new PieceBox(mock(UnderAttackRule.class));
        final PieceSerialiser ser = new PieceSerialiser(box);

        assertThat(ser.serialise(box.blackPawn)).isEqualTo("BP");
        assertThat(ser.serialise(box.blackRook)).isEqualTo("BR");
        assertThat(ser.serialise(box.blackKnight)).isEqualTo("BN");
        assertThat(ser.serialise(box.blackBishop)).isEqualTo("BB");
        assertThat(ser.serialise(box.blackQueen)).isEqualTo("BQ");
        assertThat(ser.serialise(box.blackKing)).isEqualTo("BK");
        assertThat(ser.serialise(box.whitePawn)).isEqualTo("WP");
        assertThat(ser.serialise(box.whiteRook)).isEqualTo("WR");
        assertThat(ser.serialise(box.whiteKnight)).isEqualTo("WN");
        assertThat(ser.serialise(box.whiteBishop)).isEqualTo("WB");
        assertThat(ser.serialise(box.whiteQueen)).isEqualTo("WQ");
        assertThat(ser.serialise(box.whiteKing)).isEqualTo("WK");
    }

    @Test
    void deserialise() {
        final PieceBox box = new PieceBox(mock(UnderAttackRule.class));
        final PieceSerialiser ser = new PieceSerialiser(box);

        assertThat(ser.deserialise("BP")).isEqualTo(box.blackPawn);
        assertThat(ser.deserialise("BR")).isEqualTo(box.blackRook);
        assertThat(ser.deserialise("BN")).isEqualTo(box.blackKnight);
        assertThat(ser.deserialise("BB")).isEqualTo(box.blackBishop);
        assertThat(ser.deserialise("BQ")).isEqualTo(box.blackQueen);
        assertThat(ser.deserialise("BK")).isEqualTo(box.blackKing);
        assertThat(ser.deserialise("WP")).isEqualTo(box.whitePawn);
        assertThat(ser.deserialise("WR")).isEqualTo(box.whiteRook);
        assertThat(ser.deserialise("WN")).isEqualTo(box.whiteKnight);
        assertThat(ser.deserialise("WB")).isEqualTo(box.whiteBishop);
        assertThat(ser.deserialise("WQ")).isEqualTo(box.whiteQueen);
        assertThat(ser.deserialise("WK")).isEqualTo(box.whiteKing);
    }

    @Test
    void getPieceModule() throws JsonProcessingException {
        final PieceSerialiser ser = new PieceSerialiser(new PieceBox(mock(UnderAttackRule.class)));

        final ObjectMapper om = new ObjectMapper();
        om.registerModule(ser.getPieceModule());
        om.writeValueAsString(singletonMap("u", new Rook(White)));
        om.readValue("{\"u\":\"WR\", \"b\":\"BQ\"}", new TypeReference<Map<String, Piece>>() {});

        System.out.println(ser.serialise(new Rook(White)));
        System.out.println(ser.deserialise("BB"));

        fail("We're not really doing anything here");
    }

    @AfterEach
    void tearDown() {
    }
}