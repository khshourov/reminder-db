package com.github.khshourov.reminderdb.engine;

import com.github.khshourov.reminderdb.models.TimePoint;
import java.util.ArrayList;
import java.util.List;

public class TimePointTaskExecutor {
  private final Pulser pulser;
  private final List<TimePointTask> timePointTasks;
  private TimePoint timePoint;

  public TimePointTaskExecutor(Pulser pulser) {
    this.pulser = pulser;
    this.pulser.registerCallback(this::handlePulse);
    this.timePointTasks = new ArrayList<>();
    this.timePoint = new TimePoint(1);
  }

  public void start() {
    this.pulser.start();
  }

  public void stop() {
    this.pulser.stop();
  }

  public void registerTimePointTask(TimePointTask timePointTask) {
    this.timePointTasks.add(timePointTask);
  }

  private void handlePulse() {
    for (TimePointTask timePointTask : this.timePointTasks) {
      timePointTask.execute(this.timePoint);
    }

    this.timePoint = new TimePoint(this.timePoint.value() + 1);
  }
}
