package io.github.teonistor.devschess.inter;

import com.google.common.annotations.VisibleForTesting;
import io.github.teonistor.devschess.board.Position;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import static org.apache.commons.lang3.StringUtils.strip;

public class TerminalInput implements Input {
    public static final byte[] prompt = " > ".getBytes();

    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final BufferedReader reader;

    public TerminalInput() {
        this(System.in, System.out);
    }

    @VisibleForTesting
    TerminalInput(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        // todo
        reader = new BufferedReader(new InputStreamReader(inputStream));
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
}
