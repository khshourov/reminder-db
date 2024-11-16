package com.github.khshourov.reminderdb.engine;

public interface TimePointEmitter {
  void start();

  void stop();

  void registerCallback(Runnable callback);
}
