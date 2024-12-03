package com.github.khshourov.reminderdb.testlib;

import com.github.khshourov.reminderdb.engine.multiplexer.RequestMultiplexer;
import com.github.khshourov.reminderdb.engine.requesthandlers.RequestHandler;
import com.github.khshourov.reminderdb.models.Request;
import java.util.HashMap;
import java.util.Map;

public class MockRequestMultiplexer implements RequestMultiplexer {
  private Map<Integer, RequestHandler> handlers = new HashMap<>();

  @Override
  public void registerHandler(int handlerId, RequestHandler handler) {
    if (handlers.containsKey(handlerId)) {
      throw new IllegalArgumentException(String.format("Duplicate handler-id: %d", handlerId));
    }

    this.handlers.put(handlerId, handler);
  }

  @Override
  public void start() {}

  @Override
  public void multiplex(Request request) {}

  public RequestHandler getRequestHandler(int handlerId) {
    return this.handlers.get(handlerId);
  }
}
