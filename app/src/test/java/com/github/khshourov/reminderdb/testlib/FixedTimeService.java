package com.github.khshourov.reminderdb.testlib;

import com.github.khshourov.reminderdb.interfaces.TimeService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class FixedTimeService implements TimeService {
  private static final long fixedEpochSecond = 752457600L;
  private final Clock clock;

  public FixedTimeService() {
    this.clock = Clock.fixed(Instant.ofEpochSecond(fixedEpochSecond), ZoneId.systemDefault());
  }

  @Override
  public long getCurrentEpochSecond() {
    return Instant.now(this.clock).getEpochSecond();
  }
}
