package com.github.khshourov.reminderdb.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.khshourov.reminderdb.models.TimePoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TimePointTaskExecutorTest {
  TimePointTaskExecutor taskExecutor;
  InstantTimePointEmitter timePointEmitter;

  MockTimePointTask timePointTask1;
  MockTimePointTask timePointTask2;

  @BeforeEach
  void init() {
    timePointEmitter = new InstantTimePointEmitter();
    taskExecutor = new TimePointTaskExecutor(timePointEmitter);

    timePointTask1 = new MockTimePointTask();
    timePointTask2 = new MockTimePointTask();
  }

  @Test
  void startingTaskExecutorShouldCallTimePointEmitter() {
    taskExecutor.start();

    assertTrue(timePointEmitter.startCalled);
  }

  @Test
  void stoppingTaskExecutorShouldCallTimePointEmitter() {
    taskExecutor.stop();

    assertTrue(timePointEmitter.stopCalled);
  }

  @Test
  void timePointTasksShouldExecuteForEachTimePointTick() {
    taskExecutor.registerTimePointTask(timePointTask1);
    taskExecutor.registerTimePointTask(timePointTask2);

    timePointEmitter.emit();

    TimePoint expectedCalledWith = new TimePoint(1);

    assertEquals(expectedCalledWith, timePointTask1.calledWith);
    assertEquals(expectedCalledWith, timePointTask2.calledWith);
  }

  @Test
  void eachTimePointTickShouldIncrementTimePoint() {
    taskExecutor.registerTimePointTask(timePointTask1);

    timePointEmitter.emit();

    TimePoint expectedCalledWith = new TimePoint(1);

    assertEquals(expectedCalledWith, timePointTask1.calledWith);

    timePointEmitter.emit();

    expectedCalledWith = new TimePoint(2);

    assertEquals(expectedCalledWith, timePointTask1.calledWith);
  }

  private static class InstantTimePointEmitter implements TimePointEmitter {
    public boolean startCalled;
    public boolean stopCalled;
    private Runnable callback;

    @Override
    public void start() {
      this.startCalled = true;
    }

    @Override
    public void stop() {
      this.stopCalled = true;
    }

    @Override
    public void registerCallback(Runnable callback) {
      this.callback = callback;
    }

    public void emit() {
      this.callback.run();
    }
  }

  private static class MockTimePointTask implements TimePointTask {
    public TimePoint calledWith;

    @Override
    public void execute(TimePoint timePoint) {
      this.calledWith = timePoint;
    }
  }
}
