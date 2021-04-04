package io.github.teonistor.chess.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.PieceBox;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;

import java.io.IOException;

import static java.util.function.Function.identity;

public class PieceSerialiser extends SimpleModule {

    public PieceSerialiser(final PieceBox box) {
        super("Piece Module");

        final Stream<String> strings = Stream.of("BP", "BR", "BN", "BB", "BQ", "BK", "WP", "WR", "WN", "WB", "WQ", "WK");
        final Stream<Piece> pieces = Stream.of(
                box.blackPawn, box.blackRook, box.blackKnight, box.blackBishop, box.blackQueen, box.blackKing,
                box.whitePawn, box.whiteRook, box.whiteKnight, box.whiteBishop, box.whiteQueen, box.whiteKing);

        final Map<Piece, String> pieceToStr = pieces.zip(strings).toMap(identity());
        final Map<String, Piece> strToPiece = strings.zip(pieces).toMap(identity());

        addSerializer(Piece.class, new JsonSerializer<>() {
            @Override
            public void serialize(final Piece piece, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(pieceToStr.get(piece).getOrElseThrow(() -> new UnsupportedOperationException("Cannot serialise unknown piece " + piece)));
            }
        });

        addDeserializer(Piece.class, new JsonDeserializer<>() {
            @Override
            public Piece deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
                final String pieceAsText = jsonParser.getText();
                return strToPiece.get(pieceAsText).getOrElseThrow(() -> new InvalidFormatException(jsonParser, pieceAsText + " cannot be parsed as a piece", pieceAsText, Piece.class));
            }
        });
    }
}
