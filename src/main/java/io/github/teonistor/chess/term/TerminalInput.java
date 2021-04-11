package io.github.teonistor.chess.term;

import com.google.common.annotations.VisibleForTesting;
import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.ctrl.Input;
import io.github.teonistor.chess.ctrl.InputActionProvider;
import io.github.teonistor.chess.ctrl.NewParallelGameInput;
import io.github.teonistor.chess.ctrl.NewStandardGameInput;
import io.github.teonistor.chess.ctrl.NormalGameInput;
import io.vavr.control.Option;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TerminalInput {
    static final byte[] gamePrompt = " > ".getBytes();
    static final byte[] provisionPrompt = "new/load > ".getBytes();
    static final Pattern global = Pattern.compile("\\s*((NEW(?: STANDARD| PARALLEL)?|LOAD|SAVE|EXIT)(.*)|([A-H][0-9])[\\s-]*([A-H][0-9]))\\s*", Pattern.CASE_INSENSITIVE);

    private final OutputStream outputStream;
    private final BufferedReader reader;
    private final InputActionProvider inputActionProvider;

    public TerminalInput(final InputActionProvider inputActionProvider) {
        this(System.out, new BufferedReader(new InputStreamReader(System.in)), inputActionProvider);
    }

    @VisibleForTesting
    TerminalInput(final OutputStream outputStream, final BufferedReader reader, final InputActionProvider inputActionProvider) {
        this.outputStream = outputStream;
        this.reader = reader;
        this.inputActionProvider = inputActionProvider;
    }

    public Option<Input> simpleInput() {
        try {
            outputStream.write(gamePrompt);

            final Matcher match = global.matcher(reader.readLine());
            if (match.matches()) {

                if (match.group(2) == null) {
                    return Option.some(new NormalGameInput(Position.valueOf(match.group(4).toUpperCase()), Position.valueOf(match.group(5).toUpperCase())));

                } else if ("NEW PARALLEL".equalsIgnoreCase(match.group(2))) {
                    return Option.some(new NewParallelGameInput());

                } else if ("NEW".equalsIgnoreCase(match.group(2)) || "NEW STANDARD".equalsIgnoreCase(match.group(2))) {
                    return Option.some(new NewStandardGameInput());

                } else if ("LOAD".equalsIgnoreCase(match.group(2))) {
                    return null;

                } else if ("SAVE".equalsIgnoreCase(match.group(2))) {
                    return null;

                } else if ("EXIT".equalsIgnoreCase(match.group(2))) {
                    return Option.none();

                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        // Try again...
        return simpleInput();
    }

    public String specialInput(final String... options) {
        throw new UnsupportedOperationException("When we have this we'll drink champagne");
    }
}
