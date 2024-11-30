package com.github.khshourov.reminderdb.models;

import com.github.khshourov.reminderdb.avro.AvroSchedule;

public class Schedule {
  private AvroSchedule avroSchedule;

  public Schedule(AvroSchedule avroSchedule) {
    assert avroSchedule != null;

    this.avroSchedule =
        AvroSchedule.newBuilder(avroSchedule)
            .setRemainingReminders(avroSchedule.getTotalReminders())
            .build();
  }

  public AvroSchedule avroSchedule() {
    return this.avroSchedule;
  }

  public boolean decreaseRemainingReminders() {
    if (this.avroSchedule.getRemainingReminders() == -1) {
      return true;
    }

    if (this.avroSchedule.getRemainingReminders() == 0) {
      return false;
    }

    this.avroSchedule =
        AvroSchedule.newBuilder(this.avroSchedule)
            .setRemainingReminders(this.avroSchedule.getRemainingReminders() - 1)
            .build();
    return true;
  }
}
