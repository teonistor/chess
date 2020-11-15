package io.github.teonistor.chess.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.PieceBox;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;

import java.io.IOException;
import java.util.function.Function;

import static java.util.function.Function.identity;

public class PieceSerialiser {

    private final Map<Piece, String> pieceToStr;
    private final Map<String, Piece> strToPiece;
    // Lazy-init
    private SimpleModule pieceModule;

    public PieceSerialiser(final PieceBox box) {
        final Stream<String> strings = Stream.of("BP", "BR", "BN", "BB", "BQ", "BK", "WP", "WR", "WN", "WB", "WQ", "WK");
        final Stream<Piece> pieces = Stream.of(
                box.blackPawn, box.blackRook, box.blackKnight, box.blackBishop, box.blackQueen, box.blackKing,
                box.whitePawn, box.whiteRook, box.whiteKnight, box.whiteBishop, box.whiteQueen, box.whiteKing);

        this.pieceToStr = pieces.zip(strings).toMap(identity());
        this.strToPiece = strings.zip(pieces).toMap(identity());
    }

    public String serialise(final Piece piece) {
        return pieceToStr.get(piece).getOrNull();
    }

    public Piece deserialise(final String piece) {
        return strToPiece.get(piece).getOrNull();
    }

    public Module getPieceModule() {
        if (pieceModule == null) {
            pieceModule = new SimpleModule("Piece Module");
            pieceModule.addSerializer(Piece.class, new JsonSerializer<>() {
                @Override
                public void serialize(final Piece piece, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException {
                    jsonGenerator.writeString(serialise(piece));
                }
            });
            pieceModule.addDeserializer(Piece.class, new JsonDeserializer<>() {
                @Override
                public Piece deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
                    return deserialise(jsonParser.getText());
                }
            });
        }

        return pieceModule;
    }
}
