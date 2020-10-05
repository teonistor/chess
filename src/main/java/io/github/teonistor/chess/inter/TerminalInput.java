package io.github.teonistor.chess.inter;

import com.google.common.annotations.VisibleForTesting;
import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameStateProvider;
import io.github.teonistor.chess.core.StateProvision;
import io.vavr.PartialFunction;
import io.vavr.collection.Stream;
import org.apache.commons.lang3.Functions.FailableFunction;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.exception.ExceptionUtils.rethrow;
import static org.apache.commons.lang3.StringUtils.strip;

public class TerminalInput implements Input {
    static final byte[] gamePrompt = " > ".getBytes();
    static final byte[] provisionPrompt = "new/load > ".getBytes();
    static final Pattern twoInputs = Pattern.compile("\\s*([a-hA-H][0-9])[\\s-]*([a-hA-H][0-9])\\s*");

    private final OutputStream outputStream;
    private final BufferedReader reader;

    public TerminalInput() {
        this(System.out, new BufferedReader(new InputStreamReader(System.in)));
    }

    @VisibleForTesting
    TerminalInput(OutputStream outputStream, BufferedReader reader) {
        this.outputStream = outputStream;
        this.reader = reader;
    }

    @Override
    public <T> T takeInput(Function<Position, T> callbackOne, BiFunction<Position, Position, T> callbackTwo) {
        try {
            outputStream.write(gamePrompt);
            final String line = reader.readLine();
            final Matcher match = twoInputs.matcher(line);

            if (match.matches()) {
                return callbackTwo.apply(Position.valueOf(match.group(1).toUpperCase()), Position.valueOf(match.group(2).toUpperCase()));
            }
            return callbackOne.apply(Position.valueOf(strip(line).toUpperCase()));

        } catch (final IOException | IllegalArgumentException e) {
            return callbackOne.apply(Position.OutOfBoard);
        }
    }

    @Override
    public <T> T takeInput(Function<Position, T> callback) {
        try {
            outputStream.write(gamePrompt);
            return callback.apply(Position.valueOf(strip(reader.readLine()).toUpperCase()));
        } catch (final IOException | IllegalArgumentException e) {
            return callback.apply(Position.OutOfBoard);
        }
    }

    @Override
    // TODO This method is ugly
    public GameStateProvider stateProvision(PartialFunction<StateProvision, FailableFunction<String,GameStateProvider,Exception>> stateProvision) {
        try {
            outputStream.write(provisionPrompt);
            final String[] s = strip(reader.readLine()).split(" ", 2);

            return Stream.of(StateProvision.values())
                    .filter(p -> s[0].toLowerCase().equals(p.name().toLowerCase()))
                    .map(t -> stateProvision.apply(t))
                    .head()
                    // TODO This broke "new" because it assumes arguments. Regex time...
                    .apply(s[1]);

        } catch (final Exception e) {
            return rethrow(e);
        }
    }
}
