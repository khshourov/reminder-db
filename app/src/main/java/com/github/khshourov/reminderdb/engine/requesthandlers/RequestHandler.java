package com.github.khshourov.reminderdb.engine.requesthandlers;

import com.github.khshourov.reminderdb.engine.multiplexer.RequestMultiplexer;

public interface RequestHandler {
  void register(RequestMultiplexer multiplexer);

  /** [TODO] Add a byte array as param */
  void handle();
}
