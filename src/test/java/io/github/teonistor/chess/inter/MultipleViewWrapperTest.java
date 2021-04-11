package io.github.teonistor.chess.inter;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class MultipleViewWrapperTest implements RandomPositionsTestMixin {

    private final View aView = mock(View.class);
    private final View anotherView = mock(View.class);

    @Test
    void wrapBad() {
        assertThatIllegalArgumentException().isThrownBy(MultipleViewWrapper::wrapIfNeeded)
                .withMessage("At least one view must be given");
        assertThatNullPointerException().isThrownBy(() -> MultipleViewWrapper.wrapIfNeeded(aView, null, anotherView))
                .withMessage("All views must not be null");
        assertThatNullPointerException().isThrownBy(() -> MultipleViewWrapper.wrapIfNeeded((View) null))
                .withMessage("All views must not be null");
    }

    @Test
    void wrapOne() {
        assertThat(MultipleViewWrapper.wrapIfNeeded(aView)).isEqualTo(aView);
    }

    @RepeatedTest(3)
    void wrapManyAndRefresh() {
        final HashMap<Position, Position> possibleMovesBlack = HashMap.of(randomPositions.next(), randomPositions.next());
        final HashMap<Position, Position> possibleMovesWhite = HashMap.of(randomPositions.next(), randomPositions.next());
        final View view = MultipleViewWrapper.wrapIfNeeded(aView, anotherView);
        view.refresh(HashMap.empty(), List.empty(), possibleMovesBlack, possibleMovesWhite);

        verify(aView).refresh(HashMap.empty(), List.empty(), possibleMovesBlack, possibleMovesWhite);
        verify(anotherView).refresh(HashMap.empty(), List.empty(), possibleMovesBlack, possibleMovesWhite);
    }

    @Test
    void wrapManyAndAnnounce() {
        final View view = MultipleViewWrapper.wrapIfNeeded(aView, anotherView);
        view.announce("Very important message");

        verify(aView).announce("Very important message");
        verify(anotherView).announce("Very important message");
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(aView, anotherView);
    }
}