package com.github.khshourov.reminderdb.models;

import com.github.khshourov.reminderdb.lib.utils.TimePointRange;

public record TimePoint(Integer value) implements Comparable<TimePoint> {
  @Override
  public int compareTo(TimePoint o) {
    return Integer.compare(this.value, o.value());
  }

  public boolean isLessThan(TimePoint o) {
    return this.compareTo(o) < 0;
  }

  public boolean isGreaterThanEqual(TimePoint o) {
    return this.compareTo(o) >= 0;
  }

  public boolean isBetween(TimePointRange timePointRange) {
    return this.isGreaterThanEqual(timePointRange.start()) && this.isLessThan(timePointRange.end());
  }
}
