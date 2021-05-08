package io.github.teonistor.chess.save;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameData;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.factory.Factory.GameType;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.exception.ExceptionUtils.rethrow;

@RequiredArgsConstructor
public class SaveLoad {

    private final TypeReference<List<SerializableState>> serializableStateListType = new TypeReference<>() {};
    private final ObjectMapper objectMapper;

//    public void saveState(final GameState state, final String fileName) {
//        saveState(state, new FileOutputStream(fileName));
//    }
//
//    public GameStateProvider loadState(final String fileName) {
//        return loadState(new FileInputStream(fileName));
//    }

    public void save(final GameData data, OutputStream outputStream) {
        final SerializableData serializableData = convert(data);

        try (final GZIPOutputStream gz = new GZIPOutputStream(outputStream)) {
            objectMapper.writeValue(gz, serializableData);

        } catch (final IOException e) {
            rethrow(e);
        }
    }

    private SerializableData convert(GameData data) {
        final List<SerializableState> serializableStates = deconstructStates(data.getState());
        return new SerializableData(data.getType(), serializableStates);
    }

    private List<SerializableState> deconstructStates(GameState state) {
        return Stream.iterate(state, GameState::getPrevious)
                .takeUntil(Objects::isNull)
                .map(this::deconstructState)
                .toList();
    }

    private SerializableState deconstructState(final GameState state) {
        return new SerializableState(state.getBoard(), state.getPlayer(), state.getCapturedPieces());
    }


    public GameData load(InputStream inputStream) {
        try (final GZIPInputStream gz = new GZIPInputStream(inputStream)) {
            return convert(objectMapper.readValue(gz, SerializableData.class));

        } catch (final IOException e) {
            return rethrow(e);
        }
    }

    private GameData convert(SerializableData data) {
        return new GameData(data.type, reconstructStatesRecursively(data.state, 0));
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

    @AllArgsConstructor
    @Getter
    @NoArgsConstructor(access=PRIVATE)
    private static class SerializableData {
        GameType type;
        List<SerializableState> state;
    }
}
