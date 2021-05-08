package io.github.teonistor.chess.save;

import io.github.teonistor.chess.core.GameData;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.piece.Bishop;
import io.github.teonistor.chess.piece.King;
import io.github.teonistor.chess.piece.Knight;
import io.github.teonistor.chess.piece.Pawn;
import io.github.teonistor.chess.piece.Queen;
import io.github.teonistor.chess.piece.Rook;
import io.github.teonistor.chess.spring.ChessConfig;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static io.github.teonistor.chess.core.Player.Black;
import static io.github.teonistor.chess.core.Player.White;
import static io.github.teonistor.chess.factory.Factory.GameType.PARALLEL;
import static org.assertj.core.api.Assertions.assertThat;

@MockitoSettings
class SaveLoadTest implements RandomPositionsTestMixin {

    @Test
    void saveLoad() {
        final GameData saved = new GameData(PARALLEL,
                new GameState(HashMap.of(randomPositions.next(), new Pawn(White),
                        randomPositions.next(), new Rook(Black)),
                        White,
                        List.of(new Bishop(White)),
                        new GameState(HashMap.of(randomPositions.next(), new Queen(White),
                                randomPositions.next(), new King(Black, null)),
                                White,
                                List.of(new Knight(Black)),
                                null)));

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final SaveLoad saveLoad = new SaveLoad(new ChessConfig().objectMapper());
        
        saveLoad.save(saved, bos);
        final GameData loaded = saveLoad.load(new ByteArrayInputStream(bos.toByteArray()));

        assertThat(loaded).isEqualTo(saved);
    }
}