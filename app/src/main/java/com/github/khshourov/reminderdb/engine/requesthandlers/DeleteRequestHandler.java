package com.github.khshourov.reminderdb.engine.requesthandlers;

import com.github.khshourov.reminderdb.avro.AvroResponse;
import com.github.khshourov.reminderdb.avro.AvroToken;
import com.github.khshourov.reminderdb.engine.multiplexer.RequestMultiplexer;
import com.github.khshourov.reminderdb.engine.responsehandlers.ResponseHandler;
import com.github.khshourov.reminderdb.engine.responsehandlers.ResponseType;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.lib.remindstore.RemindStore;
import com.github.khshourov.reminderdb.models.Request;
import com.github.khshourov.reminderdb.models.Response;
import com.github.khshourov.reminderdb.models.Token;

public class DeleteRequestHandler implements RequestHandler {
  private static final int handlerId = HandlerType.DELETE.ordinal();

  private RemindStore remindStore;
  private ResponseHandler responseHandler;

  public DeleteRequestHandler(RemindStore remindStore, ResponseHandler responseHandler) {
    this.remindStore = remindStore;
    this.responseHandler = responseHandler;
  }

  @Override
  public int handlerId() {
    return handlerId;
  }

  @Override
  public void register(RequestMultiplexer multiplexer) {
    multiplexer.registerHandler(handlerId, this);
  }

  @Override
  public void handle(Request request) {
    if (handlerId != request.getType()) {
      this.responseHandler.send(
          new Response(
              AvroResponse.newBuilder()
                  .setCode(ResponseType.E_0001.name())
                  .setTitle("Internal Error")
                  .build(),
              request.getUser()));
      return;
    }

    AvroToken avroToken;
    try {
      avroToken = AvroToken.fromByteBuffer(request.getPayload());
    } catch (Exception e) {
      this.responseHandler.send(
          new Response(
              AvroResponse.newBuilder()
                  .setCode(ResponseType.E_0002.name())
                  .setTitle("Payload Parsing Error")
                  .setDetails(e.getMessage())
                  .build(),
              request.getUser()));
      return;
    }

    Token token;
    try {
      token = Token.createFrom(avroToken);
    } catch (ValidationException e) {
      this.responseHandler.send(
          new Response(
              AvroResponse.newBuilder()
                  .setCode(ResponseType.E_0003.name())
                  .setTitle("Payload Validation Error")
                  .setDetails(e.getMessage())
                  .build(),
              request.getUser()));
      return;
    }

    boolean deleted = this.remindStore.delete(token);
    if (!deleted) {
      this.responseHandler.send(
          new Response(
              AvroResponse.newBuilder()
                  .setCode(ResponseType.E_0003.name())
                  .setTitle("Payload Validation Error")
                  .setDetails("invalid token")
                  .build(),
              request.getUser()));
      return;
    }

    this.responseHandler.send(
        new Response(
            AvroResponse.newBuilder().setCode(ResponseType.OK.name()).setTitle("Success").build(),
            request.getUser()));
  }
}
