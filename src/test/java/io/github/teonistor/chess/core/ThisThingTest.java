package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Rook;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import io.vavr.Tuple4;
import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@MockitoSettings
class ThisThingTest implements RandomPositionsTestMixin {

    @BeforeEach
    void setUp() {
    }

    @Test
    void construct() {
        final ThisThing thing = new ThisThing(HashMap.of(new Tuple4<>(randomPositions.next(), randomPositions.next(), randomPositions.next(), randomPositions.next()), new PartialState() {
        }));

        assertThat(thing.decide()).isEmpty();
    }

    @Test
    void stillUndecidedAfterBlackInput() {
        final Position blackFrom = randomPositions.next();
        final Position blackTo = randomPositions.next();
        final ThisThing thing = new ThisThing(HashMap.of(new Tuple4<>(randomPositions.next(), randomPositions.next(), blackFrom, blackTo), new PartialState() {}))
                .blackInput(blackFrom, blackTo);

        assertThat(thing.decide()).isEmpty();
    }

    @Test
    void stillUndecidedAfterWhiteInput() {
        final Position whiteFrom = randomPositions.next();
        final Position whiteTo = randomPositions.next();
        final ThisThing thing = new ThisThing(HashMap.of(new Tuple4<>(whiteFrom, whiteTo, randomPositions.next(), randomPositions.next()), new PartialState() {}))
                .whiteInput(whiteFrom, whiteTo);

        assertThat(thing.decide()).isEmpty();
    }

    @Test
    void decidedAfterBothInputs(final @Mock GameState state) {
        when(state.completeState()).thenReturn(Option.some(state));
        final Position whiteFrom = randomPositions.next();
        final Position whiteTo = randomPositions.next();
        final Position blackFrom = randomPositions.next();
        final Position blackTo = randomPositions.next();
        final ThisThing thing = new ThisThing(HashMap.of(new Tuple4<>(whiteFrom, whiteTo, blackFrom, blackTo), state))
                .whiteInput(whiteFrom, whiteTo)
                .blackInput(blackFrom, blackTo);

        assertThat(thing.decide()).containsExactly(state);
    }

    @Test
    void undecidedAfterBothInputsWhenTheStateThereIsPartial() {
        final Position whiteFrom = randomPositions.next();
        final Position whiteTo = randomPositions.next();
        final Position blackFrom = randomPositions.next();
        final Position blackTo = randomPositions.next();
        final ThisThing thing = new ThisThing(HashMap.of(new Tuple4<>(whiteFrom, whiteTo, blackFrom, blackTo), new PartialState() {}))
                .whiteInput(whiteFrom, whiteTo)
                .blackInput(blackFrom, blackTo);

        assertThat(thing.decide()).isEmpty();
    }

    @Test
    void throwOnImplausibleBlackInput() {
        assertThatThrownBy(()-> new ThisThing(HashMap.of(new Tuple4<>(randomPositions.next(), randomPositions.next(), randomPositions.next(), randomPositions.next()), new PartialState() {}))
                .blackInput(randomPositions.next(), randomPositions.next()))
                .isInstanceOf(ThisThing.ImplausibleInputException.class);
    }

    @Test
    void throwOnImplausibleWhiteInput() {
        assertThatThrownBy(()-> new ThisThing(HashMap.of(new Tuple4<>(randomPositions.next(), randomPositions.next(), randomPositions.next(), randomPositions.next()), new PartialState() {}))
                .whiteInput(randomPositions.next(), randomPositions.next()))
                .isInstanceOf(ThisThing.ImplausibleInputException.class);
    }

    @Test
    void throwOnRepeatedBlackInput() {
        final Position blackFrom = randomPositions.next();
        final Position blackTo = randomPositions.next();
        assertThatThrownBy(()-> new ThisThing(HashMap.of(new Tuple4<>(randomPositions.next(), randomPositions.next(), blackFrom, blackTo), new PartialState() {}))
                .blackInput(blackFrom, blackTo)
                .blackInput(randomPositions.next(), randomPositions.next()))
                .isInstanceOf(ThisThing.RepeatedInputException.class);
    }

    @Test
    void throwOnRepeatedWhiteInput() {
        final Position whiteFrom = randomPositions.next();
        final Position whiteTo = randomPositions.next();
        assertThatThrownBy(()-> new ThisThing(HashMap.of(new Tuple4<>(whiteFrom, whiteTo, randomPositions.next(), randomPositions.next()), new PartialState() {}))
                .whiteInput(whiteFrom, whiteTo)
                .whiteInput(randomPositions.next(), randomPositions.next()))
                .isInstanceOf(ThisThing.RepeatedInputException.class);
    }

    @Test
    void stuckAfterBothInputs() {
        final Position whiteFrom = randomPositions.next();
        final Position whiteTo = randomPositions.next();
        final Position blackFrom = randomPositions.next();
        final Position blackTo = randomPositions.next();
        final ThisThing thing = new ThisThing(HashMap.of(
            new Tuple4<>(whiteFrom, whiteTo, randomPositions.next(), randomPositions.next()), new PartialState() {},
            new Tuple4<>(randomPositions.next(), randomPositions.next(), blackFrom, blackTo), new PartialState() {}))
                .whiteInput(whiteFrom, whiteTo)
                .blackInput(blackFrom, blackTo);

        // TODO This is sort of an unexceptional case (practical testing will show if it's really mundane) so using an exception feels wrong
        assertThatThrownBy(thing::decide).isInstanceOf(ThisThing.StuckException.class);
    }

    @Test
    void bothInputsAndBlackPromotes(final @Mock PartialState partialState) {
        when(partialState.completeState()).thenReturn(Option.none());
        when(partialState.completeState()).thenReturn(Option.none());

        final Position whiteFrom = randomPositions.next();
        final Position whiteTo = randomPositions.next();
        final Position blackFrom = randomPositions.next();
        final Position blackTo = randomPositions.next();
        final ThisThing thing = new ThisThing(HashMap.of(new Tuple4<>(whiteFrom, whiteTo, blackFrom, blackTo), partialState))
                .whiteInput(whiteFrom, whiteTo)
                .blackInput(blackFrom, blackTo)
                .blackPromotes(new Rook(Player.Black));

        assertThat(thing.decide()).isEmpty();
    }

    @AfterEach
    void tearDown() {
    }
}