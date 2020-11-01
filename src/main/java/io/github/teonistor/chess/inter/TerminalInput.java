package io.github.teonistor.chess.inter;

import com.google.common.annotations.VisibleForTesting;
import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.ctrl.InputAction;
import io.github.teonistor.chess.ctrl.InputActionProvider;
import io.vavr.control.Try;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TerminalInput implements InputEngine, Runnable {
    static final byte[] gamePrompt = " > ".getBytes();
    static final byte[] provisionPrompt = "new/load > ".getBytes();
    static final Pattern global = Pattern.compile("\\s*((NEW|LOAD|SAVE|EXIT)(.*)|([A-H][0-9])[\\s-]*([A-H][0-9]))\\s*", Pattern.CASE_INSENSITIVE);

    private final OutputStream outputStream;
    private final BufferedReader reader;
    private final InputActionProvider inputActionProvider;
    private final Consumer<InputAction> inputActionConsumer;

    public TerminalInput(InputActionProvider inputActionProvider, Consumer<InputAction> inputActionConsumer) {
        this(System.out, new BufferedReader(new InputStreamReader(System.in)), inputActionProvider, inputActionConsumer);
    }

    @VisibleForTesting
    TerminalInput(OutputStream outputStream, BufferedReader reader, InputActionProvider inputActionProvider, Consumer<InputAction> inputActionConsumer) {
        this.outputStream = outputStream;
        this.reader = reader;
        this.inputActionProvider = inputActionProvider;
        this.inputActionConsumer = inputActionConsumer;
    }

    @Override
    public void run() {
        // TODO Global halt condition
        while(!Thread.currentThread().isInterrupted()) {
            // Try knows better what exceptions to let slide
            Try.run(this::runOnce);
        }
    }

    @VisibleForTesting
    void runOnce() throws IOException {

        outputStream.write(gamePrompt);

        final Matcher match = global.matcher(reader.readLine());
        if (match.matches()) {

            if (match.group(2) == null) {
                inputActionConsumer.accept(inputActionProvider.gameInput(Position.valueOf(match.group(4).toUpperCase()), Position.valueOf(match.group(5).toUpperCase())));

            } else if ("NEW".equalsIgnoreCase(match.group(2))) {
                inputActionConsumer.accept(inputActionProvider.newGame());

            } else if ("LOAD".equalsIgnoreCase(match.group(2))) {
                inputActionConsumer.accept(inputActionProvider.loadGame(match.group(3).strip()));

            } else if ("SAVE".equalsIgnoreCase(match.group(2))) {
                inputActionConsumer.accept(inputActionProvider.saveGame(match.group(3).strip()));

            } else if ("EXIT".equalsIgnoreCase(match.group(2))) {
                inputActionConsumer.accept(inputActionProvider.exit());

            }
        }

    }
}
