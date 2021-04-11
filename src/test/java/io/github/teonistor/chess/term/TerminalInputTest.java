package io.github.teonistor.chess.term;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.ctrl.InputAction;
import io.github.teonistor.chess.ctrl.InputActionProvider;
import io.github.teonistor.chess.ctrl.NewParallelGameInput;
import io.github.teonistor.chess.ctrl.NewStandardGameInput;
import io.github.teonistor.chess.ctrl.NormalGameInput;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@MockitoSettings
class TerminalInputTest {

    @Mock private OutputStream outputStream;
    @Mock private BufferedReader reader;
    @Mock private InputActionProvider inputActionProvider;
    @Mock private Consumer<InputAction> inputActionConsumer;
    @Mock private InputAction action;

    @InjectMocks
    private TerminalInput terminalInput;

    @ParameterizedTest
    @ValueSource(strings={"new","New","nEw Standard"})
    void newStandardGame(final String s) throws IOException {
        when(reader.readLine()).thenReturn(s);
        assertThat(terminalInput.simpleInput().get()).isInstanceOf(NewStandardGameInput.class);
    }

    @ParameterizedTest
    @ValueSource(strings={"new parallel","New Parallel"})
    void newParallelGame(final String s) throws IOException {
        when(reader.readLine()).thenReturn(s);
        assertThat(terminalInput.simpleInput().get()).isInstanceOf(NewParallelGameInput.class);
    }

    @ParameterizedTest(name="{0}")
    @CsvSource({"load foo,foo","LOAD   /home/BAR.txt,/home/BAR.txt"})
    void loadGame(final String s, final String arg) throws IOException {
        assumeTrue(false, "Dependent on SaveLoad refactor");
//        when(reader.readLine()).thenReturn(s);
//        when(inputActionProvider.loadGame(arg)).thenReturn(action);
//
//        assertThat(terminalInput.simpleInput()).isEqualTo(action);
//
//        verify(inputActionProvider).loadGame(arg);
    }

    @ParameterizedTest(name="{0}")
    @CsvSource({"save  foo.json,foo.json","Save path/to/File.txt,path/to/File.txt"})
    void saveGame(final String s, final String arg) throws IOException {
        assumeTrue(false, "Dependent on SaveLoad refactor");

//        when(reader.readLine()).thenReturn(s);
//        when(inputActionProvider.saveGame(arg)).thenReturn(action);
//
//        assertThat(terminalInput.simpleInput()).isEqualTo(action);
//
//        verify(inputActionProvider).saveGame(arg);
    }

    @Test
    void exit() throws IOException {
        when(reader.readLine()).thenReturn("exIT");

        assertThat(terminalInput.simpleInput()).isEmpty();
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
    }
// TODO Reinstate
//    @ParameterizedTest(name="{index}")
//    @ValueSource(strings={"", " ", "\t", "I9a3", "what"})
//    void garbage(final String garbage) throws IOException {
//        when(reader.readLine()).thenReturn(garbage).thenReturn(garbage).thenReturn(garbage);
//
//        assertThat(terminalInput.simpleInput()).isEqualTo(action);
//    }

    @AfterEach
    void tearDown() throws IOException {
//        verify(outputStream).write(TerminalInput.gamePrompt);

        verifyNoMoreInteractions(/*outputStream,*/ reader, inputActionProvider, inputActionConsumer, action);
    }
}
