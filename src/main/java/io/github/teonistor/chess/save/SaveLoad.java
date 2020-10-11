package io.github.teonistor.chess.save;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.GameStateProvider;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.piece.Bishop;
import io.github.teonistor.chess.piece.King;
import io.github.teonistor.chess.piece.Knight;
import io.github.teonistor.chess.piece.Pawn;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.Queen;
import io.github.teonistor.chess.piece.Rook;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.exception.ExceptionUtils.rethrow;

public class SaveLoad {
    private static SaveLoad instance;

    private final ObjectMapper objectMapper;
    private final TypeReference<java.util.List<SerializableState>> serializableStateListType;
    private final Map<Player, String> deconsPlayer;
    private final Map<Class<? extends Piece>, String> deconsPiece;
    private final Map<String, Player> reconsPlayer;
    private final Map<String, Function<Player,Piece>> reconsPiece;

    public SaveLoad() {
        objectMapper = new ObjectMapper();
        serializableStateListType = new TypeReference<>() {};

        deconsPlayer = HashMap.of(Player.White, "W", Player.Black, "B");
        deconsPiece = HashMap.of(Pawn.class, "P", Knight.class, "N", Rook.class, "R", Bishop.class, "B", Queen.class, "Q", King.class, "K");
        reconsPlayer = HashMap.of("W", Player.White, "B", Player.Black);
        reconsPiece = HashMap.of("P", Pawn::new, "N", Knight::new, "R", Rook::new, "B", Bishop::new, "Q", Queen::new, "K", King::new);
    }

    private static void init() {
        if (instance == null) {
            instance = new SaveLoad();
        }
    }

    public static void saveState(final GameState state) {
        init();
        instance.doSaveState(state, "game.js.gz");
    }

    public static GameStateProvider loadState() {
        init();
        return instance.doLoadState("bbb.json.gz");
    }

    public void doSaveState(final GameState state, final String fileName) {
        final java.util.List<SerializableState> serializableStates = Stream.iterate(state, GameState::getPrevious)
                .takeUntil(Objects::isNull)
                .map(this::deconstructState)
                .toJavaList();
        try (final GZIPOutputStream outputStream = new GZIPOutputStream(new FileOutputStream(fileName))) {
            objectMapper.writeValue(outputStream, serializableStates);
        } catch (final IOException e) {
            rethrow(e);
        }
    }

    public GameStateProvider doLoadState(final String fileName) {
        return () -> {
            try (final GZIPInputStream inputStream = new GZIPInputStream(new FileInputStream(fileName))) {
                return reconstructStatesRecursively(objectMapper.readValue(inputStream, serializableStateListType), 0);
            } catch (final IOException e) {
                return rethrow(e);
            }
        };
    }

    private SerializableState deconstructState(final GameState state) {
        return new SerializableState(
            state.getBoard().mapValues(this::deconstructPiece).toJavaMap(),
            state.getPlayer(),
            state.getCapturedPieces().map(this::deconstructPiece).toJavaList());
    }

    private String deconstructPiece(final Piece piece) {
        return deconsPlayer.get(piece.getPlayer()).get() + deconsPiece.get(piece.getClass()).get();
    }

    private GameState reconstructStatesRecursively(final java.util.List<SerializableState> ss, final int index) {
        if (index >= ss.size())
            return null;

        return reconstructState(ss.get(index), reconstructStatesRecursively(ss, index + 1));
    }

    private GameState reconstructState(final SerializableState serializableState, final GameState previousState) {
        return new GameState(
            HashMap.ofAll(serializableState.board).mapValues(this::reconstructPiece),
            serializableState.player,
            List.ofAll(serializableState.capturedPieces).map(this::reconstructPiece),
            previousState);
    }

    private Piece reconstructPiece(final String pieceStr) {
        return reconsPiece.get(pieceStr.substring(1,2)).get().apply(reconsPlayer.get(pieceStr.substring(0,1)).get());
    }

    @AllArgsConstructor
    @Getter
    @NoArgsConstructor(access=PRIVATE)
    private static class SerializableState {
        java.util.Map<Position,String> board;
        Player player;
        java.util.List<String> capturedPieces;
    }
}
