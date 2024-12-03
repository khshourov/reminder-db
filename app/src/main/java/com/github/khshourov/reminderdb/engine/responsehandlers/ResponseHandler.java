package com.github.khshourov.reminderdb.engine.responsehandlers;

import com.github.khshourov.reminderdb.models.Response;

public interface ResponseHandler {
  void send(Response response);
}
