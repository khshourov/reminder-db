package com.github.khshourov.reminderdb.engine.requesthandlers;

import com.github.khshourov.reminderdb.avro.AvroRemindRequest;
import com.github.khshourov.reminderdb.avro.AvroResponse;
import com.github.khshourov.reminderdb.engine.multiplexer.RequestMultiplexer;
import com.github.khshourov.reminderdb.engine.responsehandlers.ResponseHandler;
import com.github.khshourov.reminderdb.engine.responsehandlers.ResponseType;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.interfaces.TimeService;
import com.github.khshourov.reminderdb.lib.remindstore.RemindStore;
import com.github.khshourov.reminderdb.models.RemindRequest;
import com.github.khshourov.reminderdb.models.Request;
import com.github.khshourov.reminderdb.models.Response;
import com.github.khshourov.reminderdb.models.TimePoint;
import java.util.Optional;

public class AddRequestHandler implements RequestHandler {
  private static final int handlerId = HandlerType.ADD.ordinal();

  private final RemindStore remindStore;
  private final ResponseHandler responseHandler;
  private final TimeService timeService;

  public AddRequestHandler(
      RemindStore remindStore, ResponseHandler responseHandler, TimeService timeService) {
    this.remindStore = remindStore;
    this.responseHandler = responseHandler;
    this.timeService = timeService;
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

    AvroRemindRequest avroRemindRequest;
    try {
      avroRemindRequest = AvroRemindRequest.fromByteBuffer(request.getPayload());
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

    RemindRequest remindRequest;
    try {
      remindRequest = RemindRequest.createFrom(avroRemindRequest, request.getUser(), timeService);
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

    if (remindRequest.getToken().value() != null) {
      this.responseHandler.send(
          new Response(
              AvroResponse.newBuilder()
                  .setCode(ResponseType.E_0003.name())
                  .setTitle("Payload Validation Error")
                  .setDetails("Token can not be set in Add request")
                  .build(),
              request.getUser()));
      return;
    }

    Optional<Long> nextRemindAt = remindRequest.refreshNextRemindAt();
    if (nextRemindAt.isEmpty()) {
      this.responseHandler.send(
          new Response(
              AvroResponse.newBuilder()
                  .setCode(ResponseType.E_0003.name())
                  .setTitle("Payload Validation Error")
                  .setDetails("Scheduling is not possible")
                  .build(),
              request.getUser()));
      return;
    }

    TimePoint timePoint =
        new TimePoint((int) (nextRemindAt.get() - this.timeService.getCurrentEpochSecond()));
    // [TODO] There should be a threshold value such that (nextRemindAt - now) > threshold
    this.remindStore.set(timePoint, remindRequest);

    this.responseHandler.send(
        new Response(
            AvroResponse.newBuilder().setCode(ResponseType.OK.name()).setTitle("Success").build(),
            request.getUser()));
  }
}
