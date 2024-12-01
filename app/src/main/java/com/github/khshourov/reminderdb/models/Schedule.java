package com.github.khshourov.reminderdb.models;

import com.github.khshourov.reminderdb.avro.AvroSchedule;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.validators.AvroScheduleValidator;

public class Schedule {
  private static final AvroScheduleValidator validator = new AvroScheduleValidator();
  private final AvroSchedule avroSchedule;
  private int remainingReminders;

  private Schedule(AvroSchedule avroSchedule) {
    assert avroSchedule != null;

    this.avroSchedule = avroSchedule;
  }

  public static Schedule createFrom(AvroSchedule avroSchedule) throws ValidationException {
    validator.validate(avroSchedule);

    Schedule schedule = new Schedule(avroSchedule);
    schedule.remainingReminders = avroSchedule.getTotalReminders();

    return schedule;
  }

  public String getExpression() {
    return this.avroSchedule.getExpression();
  }

  public int getTotalReminders() {
    return this.avroSchedule.getTotalReminders();
  }

  public int getRemainingReminders() {
    return this.remainingReminders;
  }

  public boolean decreaseRemainingReminders() {
    if (this.remainingReminders == -1) {
      return true;
    }

    if (this.remainingReminders == 0) {
      return false;
    }

    this.remainingReminders = this.remainingReminders - 1;

    return true;
  }
}
