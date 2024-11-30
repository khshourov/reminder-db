package com.github.khshourov.reminderdb.validators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.github.khshourov.reminderdb.avro.AvroSchedule;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.models.Schedule;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ScheduleValidatorTest {
  private final String VALID_EXPRESSION = "0 0/5 9-17 15W 1/2 ? 2023-2025";
  private final ScheduleValidator scheduleValidator = new ScheduleValidator();

  @Test
  void avroScheduleCanNotBeNull() {
    Schedule schedule = new Schedule(null);

    Exception exception =
        assertThrows(ValidationException.class, () -> scheduleValidator.validate(schedule));
    assertEquals("avroSchedule can not be null", exception.getMessage());
  }

  @Test
  void validExpressionShouldNotThrowException() {
    AvroSchedule avroSchedule = AvroSchedule.newBuilder().setExpression(VALID_EXPRESSION).build();
    Schedule schedule = new Schedule(avroSchedule);

    assertDoesNotThrow(() -> scheduleValidator.validate(schedule));
  }

  @ParameterizedTest
  @MethodSource("invalidExpressions")
  void invalidScheduleExpressionShouldThrowException(String expression, String exceptionMessage) {
    AvroSchedule avroSchedule = AvroSchedule.newBuilder().setExpression(expression).build();
    Schedule schedule = new Schedule(avroSchedule);

    Exception exception =
        assertThrows(ValidationException.class, () -> scheduleValidator.validate(schedule));
    assertEquals(exceptionMessage, exception.getMessage());
  }

  @ParameterizedTest
  @MethodSource("validTotalReminders")
  void totalRemindersShouldBeNegative1And1ToIntegerMax(int validTotalReminders) {
    AvroSchedule avroSchedule =
        AvroSchedule.newBuilder()
            .setExpression(VALID_EXPRESSION)
            .setTotalReminders(validTotalReminders)
            .build();
    Schedule schedule = new Schedule(avroSchedule);

    assertDoesNotThrow(() -> scheduleValidator.validate(schedule));
  }

  @ParameterizedTest
  @MethodSource("invalidTotalReminders")
  void invalidTotalRemindersShouldThrowException(int invalidTotalReminders) {
    AvroSchedule avroSchedule =
        AvroSchedule.newBuilder()
            .setExpression(VALID_EXPRESSION)
            .setTotalReminders(invalidTotalReminders)
            .build();
    Schedule schedule = new Schedule(avroSchedule);

    Exception exception =
        assertThrows(ValidationException.class, () -> scheduleValidator.validate(schedule));
    assertEquals("totalReminders should be between 1 and 2147483647 or -1", exception.getMessage());
  }

  static Stream<Arguments> invalidExpressions() {
    return Stream.of(
        arguments("", "Empty expression!"),
        arguments(" ", "Empty expression!"),
        arguments("  ", "Empty expression!"),
        // Quartz expression expects 6/7 fields
        arguments("0 A 12 * *", "Cron expression contains 5 parts but we expect one of [6, 7]"),
        arguments(
            "0 0 12 * * **",
            "Failed to parse cron expression. Invalid chars in expression! Expression: ** Invalid chars: "),
        arguments(
            "0 0 12 L L ?",
            "Failed to parse cron expression. Invalid chars in expression! Expression: L Invalid chars: L"),
        arguments(
            "0 0/0 12 * * ?", "Failed to parse cron expression. Period 0 not in range [0, 59]"),
        arguments(
            "0 0 12 50 * ?", "Failed to parse cron expression. Value 50 not in range [1, 31]"),
        arguments(
            "0 0 12 * * 5#0", "Failed to parse cron expression. Value 0 not in range [1, 7]"));
  }

  static Stream<Arguments> validTotalReminders() {
    return Stream.of(arguments(-1), arguments(1), arguments(2), arguments(Integer.MAX_VALUE));
  }

  static Stream<Arguments> invalidTotalReminders() {
    return Stream.of(arguments(-2147483648), arguments(-2), arguments(0));
  }
}
