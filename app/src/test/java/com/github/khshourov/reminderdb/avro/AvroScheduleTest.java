package com.github.khshourov.reminderdb.avro;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class AvroScheduleTest {
  private static final String VALID_SCHEDULE_EXPRESSION = "0 0 * * * *";

  @Test
  void scheduleObjectCanBeBuiltJustWithExpression() {
    assertDoesNotThrow(
        () -> AvroSchedule.newBuilder().setExpression(VALID_SCHEDULE_EXPRESSION).build());

    AvroSchedule avroSchedule =
        AvroSchedule.newBuilder().setExpression(VALID_SCHEDULE_EXPRESSION).build();
    assertEquals(1, avroSchedule.getTotalReminders());
    assertEquals(1, avroSchedule.getRemainingReminders());
  }
}
