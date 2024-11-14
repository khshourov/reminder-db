package com.github.khshourov.reminderdb.lib.utils;

import com.github.khshourov.reminderdb.models.TimePoint;
import java.util.Optional;

public record TimePointRange(TimePoint start, TimePoint end) {
  public TimePointRange(TimePoint start, TimePoint end) {
    if (start != null && end != null && start.isGreaterThanEqual(end)) {
      TimePoint temp = start;
      start = end;
      end = temp;
    }

    this.start = Optional.ofNullable(start).orElse(new TimePoint(0));
    this.end = Optional.ofNullable(end).orElse(new TimePoint(Integer.MAX_VALUE));
  }
}
