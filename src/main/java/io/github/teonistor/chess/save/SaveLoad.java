package io.github.teonistor.chess.save;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.GameStateProvider;
import io.github.teonistor.chess.core.PieceSerialiser;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Lazy;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import io.vavr.jackson.datatype.VavrModule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.exception.ExceptionUtils.rethrow;

public class SaveLoad {

    private final TypeReference<List<SerializableState>> serializableStateListType;
    private final Lazy<ObjectMapper> objectMapper;

    public SaveLoad(final PieceSerialiser pieceSerialiser) {
        serializableStateListType = new TypeReference<>() {};

        objectMapper = Lazy.of(() -> {
            final ObjectMapper om = new ObjectMapper();
            om.registerModule(new VavrModule());
            om.registerModule(pieceSerialiser.getPieceModule());
            return om;
        });
    }

    public void saveState(final GameState state, final String fileName) {
        final List<SerializableState> serializableStates = Stream.iterate(state, GameState::getPrevious)
                .takeUntil(Objects::isNull)
                .map(this::deconstructState)
                .toList();
        try (final GZIPOutputStream outputStream = new GZIPOutputStream(new FileOutputStream(fileName))) {
            objectMapper.get().writeValue(outputStream, serializableStates);
        } catch (final IOException e) {
            rethrow(e);
        }
    }

    public GameStateProvider loadState(final String fileName) {
        return () -> {
            try (final GZIPInputStream inputStream = new GZIPInputStream(new FileInputStream(fileName))) {
                return reconstructStatesRecursively(objectMapper.get().readValue(inputStream, serializableStateListType), 0);
            } catch (final IOException e) {
                return rethrow(e);
            }
        };
    }

    private SerializableState deconstructState(final GameState state) {
        return new SerializableState(state.getBoard(), state.getPlayer(), state.getCapturedPieces());
    }

    private GameState reconstructStatesRecursively(final List<SerializableState> ss, final int index) {
        if (index >= ss.size())
            return null;

        return reconstructState(ss.get(index), reconstructStatesRecursively(ss, index + 1));
    }

    private GameState reconstructState(final SerializableState serializableState, final GameState previousState) {
        return new GameState(serializableState.getBoard(), serializableState.getPlayer(), serializableState.getCapturedPieces(), previousState);
    }

    @AllArgsConstructor
    @Getter
    @NoArgsConstructor(access=PRIVATE)
    private static class SerializableState {
        Map<Position, Piece> board;
        Player player;
        List<Piece> capturedPieces;
    }
}
