package com.github.khshourov.reminderdb.testlib;

import com.github.khshourov.reminderdb.interfaces.TimeService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class FixedTimeService implements TimeService {
  public static final long now = 752457600L; // Friday, November 5, 1993 12:00:00 AM
  private final Clock clock;

  public FixedTimeService() {
    this.clock = Clock.fixed(Instant.ofEpochSecond(now), ZoneId.systemDefault());
  }

  @Override
  public long getCurrentEpochSecond() {
    return Instant.now(this.clock).getEpochSecond();
  }

  public int getYear() {
    return Instant.ofEpochSecond(now).atZone(ZoneId.systemDefault()).toLocalDateTime().getYear();
  }
}
