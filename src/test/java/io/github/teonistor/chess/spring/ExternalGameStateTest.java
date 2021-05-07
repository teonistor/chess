package io.github.teonistor.chess.spring;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import io.vavr.collection.Traversable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@MockitoSettings
class ExternalGameStateTest implements RandomPositionsTestMixin {

    private @Mock Map<Position, Piece> board;
    private @Mock Traversable<Piece> capturedPieces;
    private final Tuple2<Position, Position> move1 = new Tuple2<>(randomPositions.next(), randomPositions.next());
    private final Tuple2<Position, Position> move2 = new Tuple2<>(randomPositions.next(), randomPositions.next());
    private final Tuple2<Position, Position> move3 = new Tuple2<>(randomPositions.next(), randomPositions.next());
    private final Tuple2<Position, Position> move4 = new Tuple2<>(randomPositions.next(), randomPositions.next());
    private final Tuple2<Position, Piece> provisional1 = new Tuple2<>(randomPositions.next(), mock(Piece.class));
    private final Tuple2<Position, Piece> provisional2 = new Tuple2<>(randomPositions.next(), mock(Piece.class));

    @ParameterizedTest(name = "{index})")
    @CsvSource({"false,true,false,true,false,true",
                "false,true,true,false,true,true",
                "true,false,false,true,true,true",
                "true,false,true,false,true,false"})
    void combine(final boolean bool1, final boolean bool2, final boolean bool3, final boolean bool4, final boolean bool5, final boolean bool6) {
        final ExternalGameState state1 = new ExternalGameState(board, capturedPieces, Stream.of(move1, move2), HashMap.of(provisional1), bool1, bool2);
        final ExternalGameState state2 = new ExternalGameState(board, capturedPieces, Stream.of(move3, move4), HashMap.of(provisional2), bool3, bool4);
        final ExternalGameState state3 = new ExternalGameState(board, capturedPieces, Stream.of(move1, move2, move3, move4), HashMap.ofEntries(provisional1, provisional2), bool5, bool6);

        assertThat(state1.combine(state2)).isEqualTo(state3);
    }
}