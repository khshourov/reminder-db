package com.github.khshourov.reminderdb.engine;

import com.github.khshourov.reminderdb.models.TimePoint;
import java.util.ArrayList;
import java.util.List;

public class TimePointTaskExecutor {
  private final TimePointEmitter timePointEmitter;
  private final List<TimePointTask> timePointTasks;
  private TimePoint timePoint;

  public TimePointTaskExecutor(TimePointEmitter timePointEmitter) {
    this.timePointEmitter = timePointEmitter;
    this.timePointEmitter.registerCallback(this::handlePulse);
    this.timePointTasks = new ArrayList<>();
    this.timePoint = new TimePoint(1);
  }

  public void start() {
    this.timePointEmitter.start();
  }

  public void stop() {
    this.timePointEmitter.stop();
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
