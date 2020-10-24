package io.github.teonistor.chess.inter;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.ctrl.InputAction;
import io.github.teonistor.chess.ctrl.InputActionProvider;
import org.junit.jupiter.api.AfterEach;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;


class TerminalInputTest {

    private static final Object sentinel = new Object();
    private static final BiFunction<Position,Position,Object> expectedOne = (a, b) -> {throw new AssertionError();};
    private static final Function<Position,Object> expectedTwo = ignore -> {throw new AssertionError();};

    private final OutputStream outputStream = mock(OutputStream.class);
    private final BufferedReader reader = mock(BufferedReader.class);
    private final InputActionProvider inputActionProvider = mock(InputActionProvider.class);
    private final Consumer<InputAction> inputActionConsumer = mock(Consumer.class);

//    @ParameterizedTest
//    @ValueSource(strings={"E7","A2","C5","B3","F8","D1","H4","G6"})
//    void takeOneInput(final String position) throws IOException {
//        when(reader.readLine()).thenReturn(position + " \t");
//
//        assertThat(new TerminalInput(outputStream, reader, inputActionProvider, inputActionConsumer).takeInput((Function<Position, Object>) pos -> {
//            Assertions.assertThat(pos).isEqualTo(Position.valueOf(position));
//            return sentinel;
//        })).isSameAs(sentinel);
//
//        verify(reader).readLine();
//        verify(outputStream).write(TerminalInput.gamePrompt);
//    }
//
//    @ParameterizedTest
//    @ValueSource(strings={"E6","A4","C7","B1","F5","D2","H3","G8"})
//    void takeOneInputLowercase(final String position) throws IOException {
//        when(reader.readLine()).thenReturn("   " + position.toLowerCase());
//
//        assertThat(new TerminalInput(outputStream, reader, inputActionProvider, inputActionConsumer).takeInput((Function<Position, Object>) pos -> {
//            Assertions.assertThat(pos).isEqualTo(Position.valueOf(position));
//            return sentinel;
//        })).isSameAs(sentinel);
//
//
//        verify(reader).readLine();
//        verify(outputStream).write(TerminalInput.gamePrompt);
//    }
//
//    @ParameterizedTest
//    @CsvSource({"E6","A4","C7","B1","F5","D2","H3","G8"})
//    void takeOneInputOutOfTwo(final String position) throws IOException {
//        when(reader.readLine()).thenReturn("   " + position.toLowerCase());
//
//        assertThat(new TerminalInput(outputStream, reader, inputActionProvider, inputActionConsumer).takeInput((Function<Position, Object>) pos -> {
//            Assertions.assertThat(pos).isEqualTo(Position.valueOf(position));
//            return sentinel;
//        }, expectedOne)).isSameAs(sentinel);
//
//        verify(reader).readLine();
//        verify(outputStream).write(TerminalInput.gamePrompt);
//    }
//
//    @ParameterizedTest
//    @ValueSource(strings={"", " ", "I9", "what"})
//    void takeOneInputGarbage(final String garbage) throws IOException {
//        when(reader.readLine()).thenReturn(garbage);
//
//        assertThat(new TerminalInput(outputStream, reader, inputActionProvider, inputActionConsumer).takeInput((Function<Position, Object>) pos -> {
//            Assertions.assertThat(pos).isEqualTo(Position.valueOf("OutOfBoard"));
//            return sentinel;
//        }, expectedOne)).isSameAs(sentinel);
//
//        verify(reader).readLine();
//        verify(outputStream).write(TerminalInput.gamePrompt);
//    }
//
//    @ParameterizedTest(name="{0}")
//    @MethodSource("takeTwoInputsArgs")
//    void takeTwoInputs(final String input, final Position a, final Position b) throws IOException {
//        when(reader.readLine()).thenReturn(input);
//
//        assertThat(new TerminalInput(outputStream, reader, inputActionProvider, inputActionConsumer).takeInput(expectedTwo, (BiFunction<Position, Position, Object>) (x, y) -> {
//            Assertions.assertThat(x).isEqualTo(a);
//            Assertions.assertThat(y).isEqualTo(b);
//            return sentinel;
//        })).isSameAs(sentinel);
//
//        verify(reader).readLine();
//        verify(outputStream).write(TerminalInput.gamePrompt);
//    }

    private static Stream<Object[]> takeTwoInputsArgs() {
        final Random rand = new Random();
        final String[] outer = {"  ", "", "\t", " \t  "};
        final String[] inner = {" ", "-", "  ", " -", " -  "};

        return
        stream(outer).flatMap(left ->
            stream(inner).flatMap(middle ->
                stream(outer).flatMap(right -> {
                    final Position a = Position.values()[1 + rand.nextInt(64)];
                    final Position b = Position.values()[1 + rand.nextInt(64)];

                    return stream(new Object[][] {
                        {String.format("%s%s%s%s%s", left, a, middle, b, right), a, b},
                        {String.format("%s%s%s%s%s", left, a.toString().toLowerCase(), middle, b, right), a, b},
                        {String.format("%s%s%s%s%s", left, a, middle, b.toString().toLowerCase(), right), a, b},
                    });
                })
            )
        );
    }

//    @Test
//    void stateProvision() throws IOException {
//        when(reader.readLine()).thenReturn(StateProvision.New.name().toLowerCase() + " ");
//
//        assertThat(new TerminalInput(outputStream, reader, inputActionProvider, inputActionConsumer).stateProvision()).contains(StateProvision.New);
//
//        verify(reader).readLine();
//        verify(outputStream).write(TerminalInput.provisionPrompt);
//    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(outputStream, reader);
    }
}
