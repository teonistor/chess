package io.github.teonistor.chess.inter;

import com.google.common.annotations.VisibleForTesting;
import io.github.teonistor.chess.board.Position;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.strip;

public class TerminalInput implements Input {
    static final byte[] prompt = " > ".getBytes();
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
    public Position takeInput() {
        try {
            outputStream.write(prompt);
            return Position.valueOf(strip(reader.readLine()).toUpperCase());
        } catch (IOException | IllegalArgumentException e) {
            // Deliberately so
        }
        return Position.OutOfBoard;
    }

    @Override
    public <T> T takeInput(Function<Position, T> callbackOne, BiFunction<Position, Position, T> callbackTwo) {
        try {
            outputStream.write(prompt);
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
            outputStream.write(prompt);
            return callback.apply(Position.valueOf(strip(reader.readLine()).toUpperCase()));
        } catch (final IOException | IllegalArgumentException e) {
            return callback.apply(Position.OutOfBoard);
        }
    }
}
