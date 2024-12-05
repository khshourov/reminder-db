package com.github.khshourov.reminderdb.engine.requesthandlers;

import com.github.khshourov.reminderdb.avro.AvroResponse;
import com.github.khshourov.reminderdb.avro.AvroToken;
import com.github.khshourov.reminderdb.engine.multiplexer.RequestMultiplexer;
import com.github.khshourov.reminderdb.engine.responsehandlers.ResponseType;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.interfaces.AppService;
import com.github.khshourov.reminderdb.models.Request;
import com.github.khshourov.reminderdb.models.Response;
import com.github.khshourov.reminderdb.models.Token;

public class DeleteRequestHandler implements RequestHandler {
  private static final int handlerId = HandlerType.DELETE.ordinal();

  private final AppService appService;

  public DeleteRequestHandler(AppService appService) {
    this.appService = appService;
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
      this.appService.send(
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
      this.appService.send(
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
      this.appService.send(
          new Response(
              AvroResponse.newBuilder()
                  .setCode(ResponseType.E_0003.name())
                  .setTitle("Payload Validation Error")
                  .setDetails(e.getMessage())
                  .build(),
              request.getUser()));
      return;
    }

    boolean deleted = this.appService.delete(token);
    if (!deleted) {
      this.appService.send(
          new Response(
              AvroResponse.newBuilder()
                  .setCode(ResponseType.E_0003.name())
                  .setTitle("Payload Validation Error")
                  .setDetails("invalid token")
                  .build(),
              request.getUser()));
      return;
    }

    this.appService.send(
        new Response(
            AvroResponse.newBuilder().setCode(ResponseType.OK.name()).setTitle("Success").build(),
            request.getUser()));
  }
}
