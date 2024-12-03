package com.github.khshourov.reminderdb.testlib;

import com.github.khshourov.reminderdb.engine.responsehandlers.ResponseHandler;
import com.github.khshourov.reminderdb.models.Response;

public class MockResponseHandler implements ResponseHandler {
  private Response response;

  @Override
  public void send(Response response) {
    this.response = response;
  }

  public Response getResponse() {
    return this.response;
  }
}
