package io.github.teonistor.chess.term;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.ctrl.LoadGameInput;
import io.github.teonistor.chess.ctrl.NewParallelGameInput;
import io.github.teonistor.chess.ctrl.NewStandardGameInput;
import io.github.teonistor.chess.ctrl.NormalGameInput;
import io.github.teonistor.chess.ctrl.SaveGameInput;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static io.github.teonistor.chess.term.TerminalInput.gamePrompt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@MockitoSettings
class TerminalInputTest {

    @Mock private OutputStream outputStream;
    @Mock private BufferedReader reader;
    @Mock private InputStream fileInputStream;
    @Mock private OutputStream fileOutputStream;
    private String expectedPath;

    private TerminalInput terminalInput;

    @BeforeEach
    void setUp() {
        terminalInput = new TerminalInput(outputStream, reader, path -> {
            assertThat(path).isEqualTo(expectedPath);
            return fileInputStream;
        }, path -> {
            assertThat(path).isEqualTo(expectedPath);
            return fileOutputStream;
        });
    }

    @ParameterizedTest
    @ValueSource(strings={"new","New","nEw Standard"})
    void newStandardGame(final String s) throws IOException {
        when(reader.readLine()).thenReturn(s);
        assertThat(terminalInput.simpleInput().get()).isInstanceOf(NewStandardGameInput.class);
        verify(outputStream).write(gamePrompt);
    }

    @ParameterizedTest
    @ValueSource(strings={"new parallel","New Parallel"})
    void newParallelGame(final String s) throws IOException {
        when(reader.readLine()).thenReturn(s);
        assertThat(terminalInput.simpleInput().get()).isInstanceOf(NewParallelGameInput.class);
        verify(outputStream).write(gamePrompt);
    }

    @ParameterizedTest(name="{0}")
    @CsvSource({"load foo,foo","LOAD   /home/BAR.txt,/home/BAR.txt"})
    void loadGame(final String s, final String arg) throws IOException {
        when(reader.readLine()).thenReturn(s);
        expectedPath = arg;

        assertThat(terminalInput.simpleInput()).usingFieldByFieldElementComparator().contains(new LoadGameInput(fileInputStream));
        verify(outputStream).write(gamePrompt);
    }

    @ParameterizedTest(name="{0}")
    @CsvSource({"save  foo.json,foo.json","Save path/to/File.txt,path/to/File.txt"})
    void saveGame(final String s, final String arg) throws IOException {
        when(reader.readLine()).thenReturn(s);
        expectedPath = arg;

        assertThat(terminalInput.simpleInput()).usingFieldByFieldElementComparator().contains(new SaveGameInput(fileOutputStream));
        verify(outputStream).write(gamePrompt);
    }

    @Test
    void exit() throws IOException {
        when(reader.readLine()).thenReturn("exIT");
        assertThat(terminalInput.simpleInput()).isEmpty();
        verify(outputStream).write(gamePrompt);
    }

    @Test
    void exitOnEOF() throws IOException {
        when(reader.readLine()).thenReturn(null);
        assertThat(terminalInput.simpleInput()).isEmpty();
        verify(outputStream).write(gamePrompt);
    }

    @ParameterizedTest(name="{2}")
    @CsvSource({"G2,F4,G2 - F4",
                "F8,B6,F8 B6",
                "D3,B3,D3 - B3",
                "H6,D7,H6D7",
                "B2,D1,B2-D1",
                "E1,A1,E1  A1",
                "B4,G3,B4  G3",
                "F6,A8,F6 - A8",
                "F3,C7,F3 - C7",
                "B5,D2,B5-D2",
                "A3,C4,A3 C4",
                "A7,D6,A7 D6",
                "B7,F1,B7 F1",
                "B8,H7,B8H7",
                "G7,E4,G7-E4",
                "G1,H5,G1-H5",
                "F5,C6,F5  C6",
                "D8,G6,D8G6",
                "E5,H8,E5H8",
                "G4,E2,G4  E2",
                "E6,H3,E6 H3",
                "C8,D5,C8 - D5",
                "A2,C3,A2-C3",
                "C1,G5,C1G5",
                "H2,C5,H2  C5",
                "G8,A6,G8A6",
                "E7,E3,E7E3",
                "H4,E8,H4E8",
                "C2,A5,C2A5",
                "H1,B1,H1B1"})
    void gameInput(final Position p1, final Position p2, final String s) throws IOException {
        when(reader.readLine()).thenReturn(s);

        assertThat(terminalInput.simpleInput()).containsExactly(new NormalGameInput(p1, p2));
        verify(outputStream).write(gamePrompt);
    }

    @ParameterizedTest(name="{index}")
    @ValueSource(strings={"", " ", "\t", "I9a3", "what"})
    void garbageThenExit(final String garbage) throws IOException {
        when(reader.readLine()).thenReturn(garbage).thenReturn(garbage).thenReturn("exit");

        assertThat(terminalInput.simpleInput()).isEmpty();
        verify(outputStream, times(3)).write(gamePrompt);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(outputStream, reader);
    }
}
