package com.github.khshourov.reminderdb.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.khshourov.reminderdb.avro.AvroRemindRequest;
import com.github.khshourov.reminderdb.avro.AvroSchedule;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import java.nio.ByteBuffer;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RemindRequestTest {
  private static final String VALID_EXPRESSION = "0 0/5 9-17 15W 1/2 ? 2023-2025";
  private static final ByteBuffer VALID_CONTEXT = ByteBuffer.allocate(1);
  private static final String VALID_TOKEN = "valid-token";
  private static final int VALID_PRIORITY = 13;
  private AvroSchedule schedule;
  private AvroRemindRequest validAvroRemindRequest;
  private User user;

  @BeforeEach
  void init() throws ValidationException {
    schedule = AvroSchedule.newBuilder().setExpression(VALID_EXPRESSION).build();
    validAvroRemindRequest =
        AvroRemindRequest.newBuilder()
            .setContext(VALID_CONTEXT)
            .setSchedules(List.of(schedule))
            .setToken(VALID_TOKEN)
            .setPriority(VALID_PRIORITY)
            .build();
    user = new User(1);
  }

  @Test
  void userCanNotBeNull() {
    Exception exception =
        assertThrows(
            ValidationException.class,
            () -> RemindRequest.createFrom(validAvroRemindRequest, null));

    assertEquals("user can not be null", exception.getMessage());
  }

  @Test
  void invalidAvroRemindRequestShouldThrowException() {
    AvroRemindRequest invalidAvroRemindRequest =
        AvroRemindRequest.newBuilder()
            .setContext(ByteBuffer.allocate(0))
            .setSchedules(
                List.of(AvroSchedule.newBuilder().setExpression(VALID_EXPRESSION).build()))
            .build();

    Exception exception =
        assertThrows(
            ValidationException.class,
            () -> RemindRequest.createFrom(invalidAvroRemindRequest, user));

    assertEquals("context length should be greater or equal than 1", exception.getMessage());
  }

  @Test
  void modelShouldProvideWrapperMethodsForAvroRemindRequest() throws ValidationException {
    RemindRequest remindRequest = RemindRequest.createFrom(validAvroRemindRequest, user);

    assertEquals(VALID_CONTEXT, remindRequest.getContext());
    assertIterableEquals(List.of(Schedule.createFrom(schedule)), remindRequest.getSchedules());
    assertEquals(VALID_TOKEN, remindRequest.getToken().value());
    assertEquals(VALID_PRIORITY, remindRequest.getPriority());
  }

  @Test
  void modelShouldProvideDefaultValuesForStateVariables() throws ValidationException {
    RemindRequest remindRequest = RemindRequest.createFrom(validAvroRemindRequest, user);

    assertEquals(0, remindRequest.getScheduleId());
    assertEquals(0, remindRequest.getInsertAt());
    assertEquals(0, remindRequest.getUpdateAt());
    assertEquals(0, remindRequest.getNextRemindAt());
    assertEquals(0, remindRequest.getRetryAttempted());
  }
}