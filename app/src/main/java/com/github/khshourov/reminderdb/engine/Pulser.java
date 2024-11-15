package com.github.khshourov.reminderdb.engine;

public interface Pulser {
  void start();

  void stop();

  void registerCallback(Runnable callback);
}
