package io.github.teonistor.chess.inter;

import com.google.common.annotations.VisibleForTesting;
import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameStateProvider;
import io.github.teonistor.chess.core.StateProvision;
import io.github.teonistor.chess.ctrl.InputAction;
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

import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.StringUtils.strip;
import static org.apache.commons.lang3.exception.ExceptionUtils.rethrow;

public class TerminalInput implements Input {
    static final byte[] gamePrompt = " > ".getBytes();
    static final byte[] provisionPrompt = "new/load > ".getBytes();
    static final Pattern twoInputs = Pattern.compile("\\s*([a-hA-H][0-9])[\\s-]*([a-hA-H][0-9])\\s*");
    static final Pattern global = Pattern.compile("\\s*((NEW|LOAD|SAVE|EXIT)(.*)|([A-H][0-9])[\\s-]*([A-H][0-9]))\\s*");

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
            final Matcher match = twoInputs.matcher(line.toUpperCase());

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

    @Override
    public InputAction takeCommonInput() {
        try {
            outputStream.write(gamePrompt);

            // TODO this makes the path uppercase!!!
            final Matcher match = global.matcher(reader.readLine().toUpperCase());
            // TODO How to test this without duplicating tests from InputAction?
            if (match.matches()) {

                if (match.group(2) == null) {
                    return InputAction.gameInput(Position.valueOf(match.group(4)), Position.valueOf(match.group(5)));

                } else if ("NEW".equals(match.group(2))) {
                    return InputAction.newGame();

                } else if ("LOAD".equals(match.group(2))) {
                    return InputAction.loadGame(match.group(3).strip());

                } else if ("SAVE".equals(match.group(2))) {
                    return InputAction.saveGame(match.group(3).strip());

                } else if ("EXIT".equals(match.group(2))) {
                    return InputAction.exit();

                }
            }
        } catch (final IOException | IllegalArgumentException ignore) {
        }
        // TODO not nice
        return new InputAction() {};
    }

    @Override
    public String takeSpecialInput(final String... options) {
        return null;
    }

    public static void main(String[] args) {

        see("New");
        see(" New game");
        see(" load /c/fast/foo   ");
        see("exiT exit exit EXIT");
        see("A2A4");
        see("G7 - G5");
//        System.out.println(getInputAction("New"));
//        System.out.println(getInputAction(" New game"));
//        System.out.println(getInputAction(" load /c/fast/foo   "));
//        System.out.println(getInputAction("exiT exit exit EXIT"));
//        System.out.println(getInputAction("A2A4"));
//        System.out.println(getInputAction("G7 - G5"));
    }

    private static void see(String aNew) {
        Matcher matcher = global.matcher(aNew.toUpperCase());
        matcher.matches();
        System.out.println(range(2, matcher.groupCount() + 1).mapToObj(matcher::group).collect(joining(" | ")));
    }
}
