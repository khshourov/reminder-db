package com.github.khshourov.reminderdb.avro;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.khshourov.reminderdb.exceptions.ValidationException;
import java.nio.ByteBuffer;
import java.util.List;
import org.apache.avro.AvroRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AvroRemindRequestTest {
  private static final String VALID_EXPRESSION = "0 0/5 9-17 15W 1/2 ? 2023-2025";
  private AvroSchedule schedule;
  private ByteBuffer validContext;

  @BeforeEach
  void init() throws ValidationException {
    schedule = AvroSchedule.newBuilder().setExpression(VALID_EXPRESSION).build();

    validContext = ByteBuffer.allocate(1);
  }

  @Test
  void contextCanNotBeNull() {
    assertThrows(
        AvroRuntimeException.class,
        () ->
            AvroRemindRequest.newBuilder()
                .setContext(null)
                .setSchedules(List.of(schedule))
                .build());
  }

  @Test
  void schedulesCanNotBeNull() {
    assertThrows(
        AvroRuntimeException.class,
        () -> AvroRemindRequest.newBuilder().setContext(validContext).setSchedules(null).build());
  }

  @Test
  void tokenCanBeNullOrSkipped() {
    assertDoesNotThrow(
        () ->
            AvroRemindRequest.newBuilder()
                .setContext(validContext)
                .setSchedules(List.of(schedule))
                .build());

    assertDoesNotThrow(
        () ->
            AvroRemindRequest.newBuilder()
                .setContext(validContext)
                .setSchedules(List.of(schedule))
                .setToken(null)
                .build());

    assertNull(
        AvroRemindRequest.newBuilder()
            .setContext(validContext)
            .setSchedules(List.of(schedule))
            .build()
            .getToken());
  }

  @Test
  void priorityHasDefaultValueOfOne() {
    assertDoesNotThrow(
        () ->
            AvroRemindRequest.newBuilder()
                .setContext(validContext)
                .setSchedules(List.of(schedule))
                .build());

    assertEquals(
        1,
        AvroRemindRequest.newBuilder()
            .setContext(validContext)
            .setSchedules(List.of(schedule))
            .build()
            .getPriority());
  }
}
