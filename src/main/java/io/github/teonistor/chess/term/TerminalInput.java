package io.github.teonistor.chess.term;

import com.google.common.annotations.VisibleForTesting;
import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.ctrl.InputAction;
import io.github.teonistor.chess.ctrl.InputActionProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TerminalInput {
    static final byte[] gamePrompt = " > ".getBytes();
    static final byte[] provisionPrompt = "new/load > ".getBytes();
    static final Pattern global = Pattern.compile("\\s*((NEW|LOAD|SAVE|EXIT)(.*)|([A-H][0-9])[\\s-]*([A-H][0-9]))\\s*", Pattern.CASE_INSENSITIVE);

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

    public InputAction simpleInput() {
        try {
            outputStream.write(gamePrompt);

            final Matcher match = global.matcher(reader.readLine());
            if (match.matches()) {

                if (match.group(2) == null) {
                    return inputActionProvider.gameInput(Position.valueOf(match.group(4).toUpperCase()), Position.valueOf(match.group(5).toUpperCase()));

                } else if ("NEW".equalsIgnoreCase(match.group(2))) {
                    return inputActionProvider.newGame();

                } else if ("LOAD".equalsIgnoreCase(match.group(2))) {
                    return inputActionProvider.loadGame(match.group(3).strip());

                } else if ("SAVE".equalsIgnoreCase(match.group(2))) {
                    return inputActionProvider.saveGame(match.group(3).strip());

                } else if ("EXIT".equalsIgnoreCase(match.group(2))) {
                    return inputActionProvider.exit();

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
