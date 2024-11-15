package com.github.khshourov.reminderdb.engine;

import com.github.khshourov.reminderdb.models.TimePoint;

@FunctionalInterface
public interface TimePointTask {
  void execute(TimePoint timePoint);
}
