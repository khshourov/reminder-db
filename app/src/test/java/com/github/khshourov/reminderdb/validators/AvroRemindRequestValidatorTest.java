package com.github.khshourov.reminderdb.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.github.khshourov.reminderdb.avro.AvroRemindRequest;
import com.github.khshourov.reminderdb.avro.AvroSchedule;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class AvroRemindRequestValidatorTest {
  private static final String VALID_EXPRESSION = "0 0/5 9-17 15W 1/2 ? 2023-2025";
  private AvroSchedule schedule1;
  private AvroSchedule schedule2;
  private ByteBuffer validContext;
  private AvroRemindRequestValidator validator;

  @BeforeEach
  void init() throws ValidationException {
    schedule1 = AvroSchedule.newBuilder().setExpression(VALID_EXPRESSION).build();
    schedule2 = AvroSchedule.newBuilder().setExpression(VALID_EXPRESSION).build();

    validContext = ByteBuffer.allocate(1);

    validator = new AvroRemindRequestValidator();
  }

  @Test
  void avroRemindRequestShouldNotBeNull() {
    Exception exception = assertThrows(ValidationException.class, () -> validator.validate(null));
    assertEquals("avroRemindRequest can not be null", exception.getMessage());
  }

  @Test
  void contextLengthShouldBeGreaterOrEqualThan1() {
    AvroRemindRequest avroRemindRequest =
        AvroRemindRequest.newBuilder()
            .setContext(ByteBuffer.allocate(0))
            .setSchedules(List.of(schedule1, schedule2))
            .build();

    Exception exception =
        assertThrows(ValidationException.class, () -> validator.validate(avroRemindRequest));
    assertEquals("context length should be greater or equal than 1", exception.getMessage());
  }

  @Test
  void scheduleListShouldContainAtLeastOneEntry() {
    AvroRemindRequest avroRemindRequest =
        AvroRemindRequest.newBuilder().setContext(validContext).setSchedules(List.of()).build();

    Exception exception =
        assertThrows(ValidationException.class, () -> validator.validate(avroRemindRequest));
    assertEquals("schedules length should be greater or equal than 1", exception.getMessage());
  }

  @Test
  void invalidSchedulesShouldThrowException() {
    AvroSchedule invalidSchedule = AvroSchedule.newBuilder().setExpression("").build();

    AvroRemindRequest avroRemindRequest =
        AvroRemindRequest.newBuilder()
            .setContext(ByteBuffer.allocate(1))
            .setSchedules(List.of(schedule1, invalidSchedule))
            .build();

    Exception exception =
        assertThrows(ValidationException.class, () -> validator.validate(avroRemindRequest));
    assertEquals("Empty expression!", exception.getMessage());
  }

  @ParameterizedTest
  @MethodSource("invalidPriority")
  void priorityShouldBeBetween1And255(int invalidPriority) {
    AvroRemindRequest avroRemindRequest =
        AvroRemindRequest.newBuilder()
            .setContext(validContext)
            .setSchedules(List.of(schedule1))
            .setPriority(invalidPriority)
            .build();

    Exception exception =
        assertThrows(ValidationException.class, () -> validator.validate(avroRemindRequest));
    assertEquals("priority should be between 1 and 255", exception.getMessage());
  }

  static Stream<Arguments> invalidPriority() {
    return Stream.of(
        arguments(Integer.MIN_VALUE),
        arguments(-1),
        arguments(0),
        arguments(256),
        arguments(Integer.MAX_VALUE));
  }
}
