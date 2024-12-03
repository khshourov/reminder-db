package com.github.khshourov.reminderdb.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.khshourov.reminderdb.avro.AvroSchedule;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.testlib.FixedTimeService;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class ScheduleTest {
  private final String VALID_EXPRESSION = "0 0/5 9-17 15W 1/2 ? 2023-2025";
  private final String INVALID_EXPRESSION = "";

  @Test
  void invalidAvroScheduleShouldThrowException() {
    AvroSchedule avroSchedule = AvroSchedule.newBuilder().setExpression(INVALID_EXPRESSION).build();

    assertThrows(ValidationException.class, () -> Schedule.createFrom(avroSchedule));
  }

  @Test
  void remainingRemindersShouldBeSetWhenCreatingScheduleInstance() throws ValidationException {
    AvroSchedule avroSchedule =
        AvroSchedule.newBuilder().setExpression(VALID_EXPRESSION).setTotalReminders(10).build();
    Schedule schedule = Schedule.createFrom(avroSchedule);

    assertEquals(10, schedule.getRemainingReminders());
  }

  @Test
  void accessorMethodsShouldRetrieveCorrectValue() throws ValidationException {
    AvroSchedule avroSchedule =
        AvroSchedule.newBuilder().setExpression(VALID_EXPRESSION).setTotalReminders(1).build();
    Schedule schedule = Schedule.createFrom(avroSchedule);

    assertEquals(VALID_EXPRESSION, schedule.getExpression());
    assertEquals(1, schedule.getTotalReminders());
    assertEquals(1, schedule.getRemainingReminders());
  }

  @Test
  void twoScheduleShouldBeEqualIfContainsSameValue() throws ValidationException {
    AvroSchedule avroSchedule1 =
        AvroSchedule.newBuilder().setExpression(VALID_EXPRESSION).setTotalReminders(1).build();
    AvroSchedule avroSchedule2 =
        AvroSchedule.newBuilder().setExpression(VALID_EXPRESSION).setTotalReminders(1).build();

    Schedule schedule1 = Schedule.createFrom(avroSchedule1);
    Schedule schedule2 = Schedule.createFrom(avroSchedule2);

    assertEquals(schedule1, schedule2);
  }

  @Test
  void twoDifferentScheduleShouldNotBeEqual() throws ValidationException {
    AvroSchedule avroSchedule1 =
        AvroSchedule.newBuilder().setExpression(VALID_EXPRESSION).setTotalReminders(1).build();
    AvroSchedule avroSchedule2 =
        AvroSchedule.newBuilder().setExpression(VALID_EXPRESSION).setTotalReminders(2).build();

    Schedule schedule1 = Schedule.createFrom(avroSchedule1);
    Schedule schedule2 = Schedule.createFrom(avroSchedule2);

    assertNotEquals(schedule1, schedule2);
    assertNotEquals(null, schedule1);
    assertNotEquals(new Object(), schedule1);
  }

  @Test
  void nextScheduleEpochShouldBeInFuture() throws ValidationException {
    long currentSchedule = FixedTimeService.now;
    long now = currentSchedule;
    Schedule schedule =
        Schedule.createFrom(AvroSchedule.newBuilder().setExpression("0 * * * * ?").build());

    Optional<Long> nextSchedule = schedule.getNextSchedule(currentSchedule, now);

    assertTrue(nextSchedule.isPresent());
    assertEquals(60, nextSchedule.get() - now);
    assertEquals(0, schedule.getRemainingReminders());
  }

  @Test
  void nextScheduleEpochShouldBeGreaterThanCurrentEpoch() throws ValidationException {
    long currentSchedule = FixedTimeService.now;
    long now = currentSchedule + 2 * 60; // Must skip 2 next-schedule to be greater than now
    Schedule schedule =
        Schedule.createFrom(
            AvroSchedule.newBuilder().setExpression("0 * * * * ?").setTotalReminders(3).build());

    Optional<Long> nextSchedule = schedule.getNextSchedule(currentSchedule, now);

    assertTrue(nextSchedule.isPresent());
    assertEquals(60, nextSchedule.get() - now);
    assertEquals(0, schedule.getRemainingReminders());
  }

  @Test
  void nextScheduleEpochShouldBeEmptyWhenCronCanNotGenerateNewEpoch() throws ValidationException {
    FixedTimeService timeService = new FixedTimeService();
    long currentSchedule = timeService.getCurrentEpochSecond();
    long now = currentSchedule;
    long previousYear = timeService.getYear() - 1;

    Schedule schedule =
        Schedule.createFrom(
            AvroSchedule.newBuilder()
                .setExpression("0 * * * * ? " + previousYear)
                .setTotalReminders(3)
                .build());

    Optional<Long> nextSchedule = schedule.getNextSchedule(currentSchedule, now);

    assertTrue(nextSchedule.isEmpty());
    assertEquals(2, schedule.getRemainingReminders());
  }

  @Test
  void decreasingTotalRemindersShouldReturnTrueWhenItsNotAlreadyZero() throws ValidationException {
    long currentSchedule = FixedTimeService.now;
    long now = currentSchedule;
    Schedule schedule =
        Schedule.createFrom(
            AvroSchedule.newBuilder().setExpression("0 * * * * ?").setTotalReminders(1).build());

    Optional<Long> nextSchedule = schedule.getNextSchedule(currentSchedule, now);
    assertTrue(nextSchedule.isPresent());

    nextSchedule = schedule.getNextSchedule(currentSchedule, now);
    assertFalse(nextSchedule.isPresent());

    assertEquals(0, schedule.getRemainingReminders());
  }

  @Test
  void infiniteRemindersConfigurationWillAlwaysReturnsTrue() throws ValidationException {
    long currentSchedule = FixedTimeService.now;
    long now = currentSchedule;
    Schedule schedule =
        Schedule.createFrom(
            AvroSchedule.newBuilder().setExpression("0 * * * * ?").setTotalReminders(-1).build());

    Optional<Long> nextSchedule = schedule.getNextSchedule(currentSchedule, now);
    assertTrue(nextSchedule.isPresent());

    nextSchedule = schedule.getNextSchedule(currentSchedule, now);
    assertTrue(nextSchedule.isPresent());

    assertEquals(-1, schedule.getTotalReminders());
    assertEquals(-1, schedule.getRemainingReminders());
  }
}
