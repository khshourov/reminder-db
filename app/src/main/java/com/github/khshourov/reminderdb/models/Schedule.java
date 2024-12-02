package com.github.khshourov.reminderdb.models;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.github.khshourov.reminderdb.avro.AvroSchedule;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.validators.AvroScheduleValidator;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

public class Schedule {
  private static final AvroScheduleValidator validator = new AvroScheduleValidator();
  private final AvroSchedule avroSchedule;

  private ExecutionTime executionTime;
  private int remainingReminders;

  private Schedule(AvroSchedule avroSchedule) {
    assert avroSchedule != null;

    this.avroSchedule = avroSchedule;

    this.executionTime =
        ExecutionTime.forCron(
            (new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ)))
                .parse(avroSchedule.getExpression()));
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

  public Optional<Long> getNextSchedule(long currentSchedule, long currentEpochSecond) {
    /*
    There's a potential long loop if currentSchedule is far, far behind than currentEpochSecond.
    It's better to discard those RemindRequest that's outside a threshold value.
     */
    while (this.decreaseRemainingReminders()) {
      // [TODO] ZoneId should be configurable
      Optional<ZonedDateTime> nextExecution =
          this.executionTime.nextExecution(
              Instant.ofEpochSecond(currentSchedule).atZone(ZoneId.systemDefault()));

      if (nextExecution.isEmpty()) {
        break;
      }

      if (nextExecution.get().toEpochSecond() > currentEpochSecond) {
        return Optional.of(nextExecution.get().toEpochSecond());
      }

      currentSchedule = nextExecution.get().toEpochSecond();
    }

    return Optional.empty();
  }

  private boolean decreaseRemainingReminders() {
    if (this.remainingReminders == -1) {
      return true;
    }

    if (this.remainingReminders == 0) {
      return false;
    }

    this.remainingReminders = this.remainingReminders - 1;

    return true;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Schedule other)) {
      return false;
    }

    if (!this.avroSchedule.getExpression().equals(other.avroSchedule.getExpression())) {
      return false;
    }

    if (this.avroSchedule.getTotalReminders() != other.avroSchedule.getTotalReminders()) {
      return false;
    }

    return this.remainingReminders == other.remainingReminders;
  }
}
