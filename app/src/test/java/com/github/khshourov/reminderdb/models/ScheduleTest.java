package com.github.khshourov.reminderdb.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.khshourov.reminderdb.avro.AvroSchedule;
import org.junit.jupiter.api.Test;

public class ScheduleTest {
  private final String VALID_EXPRESSION = "0 0/5 9-17 15W 1/2 ? 2023-2025";

  @Test
  void remainingRemindersShouldBeSetWhenCreatingScheduleInstance() {
    AvroSchedule avroSchedule =
        AvroSchedule.newBuilder().setExpression(VALID_EXPRESSION).setTotalReminders(10).build();
    Schedule schedule = new Schedule(avroSchedule);

    assertEquals(10, schedule.avroSchedule().getRemainingReminders());
  }

  @Test
  void decreasingTotalRemindersShouldReturnTrueWhenItsNotAlreadyZero() {
    AvroSchedule avroSchedule =
        AvroSchedule.newBuilder().setExpression(VALID_EXPRESSION).setTotalReminders(1).build();
    Schedule schedule = new Schedule(avroSchedule);

    assertTrue(schedule.decreaseRemainingReminders());
    assertFalse(schedule.decreaseRemainingReminders());
    assertEquals(0, schedule.avroSchedule().getRemainingReminders());
  }

  @Test
  void infiniteRemindersConfigurationWillAlwaysReturnsTrue() {
    AvroSchedule avroSchedule =
        AvroSchedule.newBuilder().setExpression(VALID_EXPRESSION).setTotalReminders(-1).build();
    Schedule schedule = new Schedule(avroSchedule);

    assertTrue(schedule.decreaseRemainingReminders());
    assertTrue(schedule.decreaseRemainingReminders());
    assertEquals(-1, schedule.avroSchedule().getTotalReminders());
    assertEquals(-1, schedule.avroSchedule().getRemainingReminders());
  }
}
