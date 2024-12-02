package com.github.khshourov.reminderdb.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.khshourov.reminderdb.avro.AvroRemindRequest;
import com.github.khshourov.reminderdb.avro.AvroSchedule;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.interfaces.TimeService;
import com.github.khshourov.reminderdb.testlib.FixedTimeService;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RemindRequestTest {
  private static final String VALID_EXPRESSION = "0 0/5 9-17 15W 1/2 ? 2023-2025";
  private static final ByteBuffer VALID_CONTEXT = ByteBuffer.allocate(1);
  private static final String VALID_TOKEN = "valid-token";
  private static final int VALID_PRIORITY = 13;
  private static final int MAX_RETRY = 3;
  private AvroSchedule schedule;
  private AvroRemindRequest validAvroRemindRequest;
  private User user;
  private TimeService timeService;

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
    timeService = new FixedTimeService();
  }

  @Test
  void userCanNotBeNull() {
    Exception exception =
        assertThrows(
            ValidationException.class,
            () -> RemindRequest.createFrom(validAvroRemindRequest, null, timeService));

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
            () -> RemindRequest.createFrom(invalidAvroRemindRequest, user, timeService));

    assertEquals("context length should be greater or equal than 1", exception.getMessage());
  }

  @Test
  void modelShouldProvideWrapperMethodsForAvroRemindRequest() throws ValidationException {
    RemindRequest remindRequest =
        RemindRequest.createFrom(validAvroRemindRequest, user, timeService);

    assertEquals(VALID_CONTEXT, remindRequest.getContext());
    assertIterableEquals(List.of(Schedule.createFrom(schedule)), remindRequest.getSchedules());
    assertEquals(VALID_TOKEN, remindRequest.getToken().value());
    assertEquals(VALID_PRIORITY, remindRequest.getPriority());
  }

  @Test
  void modelShouldProvideDefaultValuesForStateVariables() throws ValidationException {
    RemindRequest remindRequest =
        RemindRequest.createFrom(validAvroRemindRequest, user, timeService);

    assertEquals(timeService.getCurrentEpochSecond(), remindRequest.getInsertAt());
    assertEquals(timeService.getCurrentEpochSecond(), remindRequest.getUpdateAt());
    assertEquals(timeService.getCurrentEpochSecond(), remindRequest.getNextRemindAt());
    assertEquals(0, remindRequest.getRetryAttempted());
  }

  @Test
  void remindRequestCanBeRetriedUpToMaxRetry() throws ValidationException {
    RemindRequest remindRequest =
        RemindRequest.createFrom(validAvroRemindRequest, user, timeService);

    assertTrue(remindRequest.canRetry(MAX_RETRY));
    assertTrue(remindRequest.canRetry(MAX_RETRY));
    assertTrue(remindRequest.canRetry(MAX_RETRY));
    assertFalse(remindRequest.canRetry(MAX_RETRY));
  }

  @Test
  void clearingRetryAttemptedShouldMakeItValueToZero() throws ValidationException {
    RemindRequest remindRequest =
        RemindRequest.createFrom(validAvroRemindRequest, user, timeService);
    remindRequest.canRetry(MAX_RETRY);
    remindRequest.canRetry(MAX_RETRY);

    assertEquals(2, remindRequest.getRetryAttempted());

    remindRequest.clearRetryAttempted();

    assertEquals(0, remindRequest.getRetryAttempted());
  }

  @Test
  void nextExecutionTimeKeepsGeneratingUntilNoScheduleIsLeft() throws ValidationException {
    AvroSchedule schedule1 =
        AvroSchedule.newBuilder().setExpression("0 * * * * ?").setTotalReminders(1).build();
    AvroSchedule schedule2 =
        AvroSchedule.newBuilder().setExpression("0 * * * * ?").setTotalReminders(1).build();

    RemindRequest remindRequest =
        RemindRequest.createFrom(
            AvroRemindRequest.newBuilder()
                .setContext(VALID_CONTEXT)
                .setSchedules(List.of(schedule1, schedule2))
                .build(),
            user,
            timeService);

    Optional<Long> nextRemindAt = remindRequest.refreshNextRemindAt();
    assertTrue(nextRemindAt.isPresent());
    assertEquals(60, nextRemindAt.get() - timeService.getCurrentEpochSecond());

    nextRemindAt = remindRequest.refreshNextRemindAt();
    assertTrue(nextRemindAt.isPresent());
    assertEquals(120, nextRemindAt.get() - timeService.getCurrentEpochSecond());

    nextRemindAt = remindRequest.refreshNextRemindAt();
    assertFalse(nextRemindAt.isPresent());
  }
}
