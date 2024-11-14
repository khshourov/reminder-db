package com.github.khshourov.reminderdb.lib.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.github.khshourov.reminderdb.models.TimePoint;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TimePointRangeTest {

  @ParameterizedTest
  @MethodSource("validArgumentsProvider")
  void shouldReturnTrueWhenInRange(TimePoint start, TimePoint end, TimePoint testPoint) {
    TimePointRange timePointRange = new TimePointRange(start, end);

    assertTrue(testPoint.isBetween(timePointRange));
  }

  @ParameterizedTest
  @MethodSource("invalidArgumentsProvider")
  void shouldReturnFalseWhenOutOfRange(TimePoint start, TimePoint end, TimePoint testPoint) {
    TimePointRange timePointRange = new TimePointRange(start, end);

    assertFalse(testPoint.isBetween(timePointRange));
  }

  static Stream<Arguments> validArgumentsProvider() {
    return Stream.of(
        arguments(new TimePoint(1), new TimePoint(10), new TimePoint(1)),
        arguments(new TimePoint(1), new TimePoint(10), new TimePoint(9)),
        arguments(new TimePoint(1), new TimePoint(10), new TimePoint(5)),
        arguments(null, new TimePoint(10), new TimePoint(5)), // start = 0 if null provided
        arguments(new TimePoint(1), null, new TimePoint(100)), // end = INTEGER_MAX if null provided
        arguments(null, null, new TimePoint(10)),
        arguments(new TimePoint(10), new TimePoint(1), new TimePoint(5)));
  }

  static Stream<Arguments> invalidArgumentsProvider() {
    return Stream.of(
        arguments(new TimePoint(5), new TimePoint(10), new TimePoint(4)),
        arguments(new TimePoint(1), new TimePoint(10), new TimePoint(10)));
  }
}
