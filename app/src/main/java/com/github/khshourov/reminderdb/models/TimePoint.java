package com.github.khshourov.reminderdb.models;

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
}
