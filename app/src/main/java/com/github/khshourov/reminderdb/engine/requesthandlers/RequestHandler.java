package com.github.khshourov.reminderdb.engine.requesthandlers;

import com.github.khshourov.reminderdb.engine.multiplexer.RequestMultiplexer;
import java.nio.ByteBuffer;

public interface RequestHandler {
  void register(RequestMultiplexer multiplexer);

  void handle(ByteBuffer payload);
}
