package com.github.khshourov.reminderdb.engine.multiplexer;

import com.github.khshourov.reminderdb.engine.requesthandlers.RequestHandler;
import com.github.khshourov.reminderdb.models.Request;

public interface RequestMultiplexer {
  void registerHandler(int handlerId, RequestHandler handler);

  void start();

  void multiplex(Request request);
}
