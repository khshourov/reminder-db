package com.github.khshourov.reminderdb.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.khshourov.reminderdb.avro.AvroSchedule;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
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
  void decreasingTotalRemindersShouldReturnTrueWhenItsNotAlreadyZero() throws ValidationException {
    AvroSchedule avroSchedule =
        AvroSchedule.newBuilder().setExpression(VALID_EXPRESSION).setTotalReminders(1).build();
    Schedule schedule = Schedule.createFrom(avroSchedule);

    assertTrue(schedule.decreaseRemainingReminders());
    assertFalse(schedule.decreaseRemainingReminders());
    assertEquals(0, schedule.getRemainingReminders());
  }

  @Test
  void infiniteRemindersConfigurationWillAlwaysReturnsTrue() throws ValidationException {
    AvroSchedule avroSchedule =
        AvroSchedule.newBuilder().setExpression(VALID_EXPRESSION).setTotalReminders(-1).build();
    Schedule schedule = Schedule.createFrom(avroSchedule);

    assertTrue(schedule.decreaseRemainingReminders());
    assertTrue(schedule.decreaseRemainingReminders());
    assertEquals(-1, schedule.getTotalReminders());
    assertEquals(-1, schedule.getRemainingReminders());
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
}
