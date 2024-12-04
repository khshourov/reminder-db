package com.github.khshourov.reminderdb.engine.requesthandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.github.khshourov.reminderdb.avro.AvroRequest;
import com.github.khshourov.reminderdb.engine.responsehandlers.ResponseType;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.lib.remindstore.RemindStore;
import com.github.khshourov.reminderdb.lib.tokenbuilder.TokenBuilder;
import com.github.khshourov.reminderdb.lib.utils.TimePointRange;
import com.github.khshourov.reminderdb.models.RemindRequest;
import com.github.khshourov.reminderdb.models.Request;
import com.github.khshourov.reminderdb.models.Response;
import com.github.khshourov.reminderdb.models.TimePoint;
import com.github.khshourov.reminderdb.models.Token;
import com.github.khshourov.reminderdb.models.User;
import com.github.khshourov.reminderdb.testlib.FixedTimeService;
import com.github.khshourov.reminderdb.testlib.MockRequestMultiplexer;
import com.github.khshourov.reminderdb.testlib.MockResponseHandler;
import com.github.khshourov.reminderdb.testlib.requestbuilders.RemindRequestBuilder;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AddRequestHandlerTest {
  private MockRequestMultiplexer multiplexer;
  private MockRemindStore remindStore;
  private MockResponseHandler responseHandler;
  private AddRequestHandler requestHandler;
  private User user;
  private FixedTimeService timeService;

  @BeforeEach
  void init() {
    multiplexer = new MockRequestMultiplexer();
    remindStore = new MockRemindStore();
    responseHandler = new MockResponseHandler();
    timeService = new FixedTimeService();
    requestHandler = new AddRequestHandler(remindStore, responseHandler, timeService);
    user = new User(1);
  }

  @Test
  void handlerIdShouldBeOfTypeAdd() {
    assertEquals(HandlerType.ADD.ordinal(), requestHandler.handlerId());
  }

  @Test
  void requestHandlerShouldRegisterItselfToRequestMultiplexer() {
    requestHandler.register(multiplexer);

    assertEquals(requestHandler, multiplexer.getRequestHandler(requestHandler.handlerId()));
  }

  @Test
  void requestTypeShouldBeAddRequest() throws ValidationException, IOException {
    AvroRequest avroRequest = new RemindRequestBuilder().withType(HandlerType.DELETE).build();
    Request request = Request.createFrom(avroRequest, user);

    requestHandler.handle(request);

    Response response = responseHandler.getResponse();
    assertEquals(ResponseType.E_0001.name(), response.payload().getCode());
    assertEquals("Internal Error", response.payload().getTitle());
    assertNull(response.payload().getData());
  }

  @Test
  void payloadShouldBeValidAvroRequest() throws ValidationException, IOException {
    AvroRequest avroRequest =
        AvroRequest.newBuilder()
            .setType(HandlerType.ADD.ordinal())
            .setPayload(ByteBuffer.allocate(1))
            .build();
    Request request = Request.createFrom(avroRequest, user);

    requestHandler.handle(request);

    Response response = responseHandler.getResponse();
    assertEquals(ResponseType.E_0002.name(), response.payload().getCode());
    assertEquals("Payload Parsing Error", response.payload().getTitle());
    assertEquals("Not enough header bytes", response.payload().getDetails());
    assertNull(response.payload().getData());
  }

  @Test
  void payloadShouldBeValidRemindRequest() throws ValidationException, IOException {
    AvroRequest avroRequest =
        new RemindRequestBuilder().withType(HandlerType.ADD).withScheduleExpression("").build();
    Request request = Request.createFrom(avroRequest, user);

    requestHandler.handle(request);

    Response response = responseHandler.getResponse();
    assertEquals(ResponseType.E_0003.name(), response.payload().getCode());
    assertEquals("Payload Validation Error", response.payload().getTitle());
    assertEquals("Empty expression!", response.payload().getDetails());
    assertNull(response.payload().getData());
  }

  @Test
  void remindRequestCanNotBeAddedIfSchedulingIsNotAvailable()
      throws ValidationException, IOException {
    int previousYear = timeService.getYear() - 1;
    AvroRequest avroRequest =
        new RemindRequestBuilder()
            .withType(HandlerType.ADD)
            .withScheduleExpression("0 * * * * ? " + previousYear)
            .build();
    Request request = Request.createFrom(avroRequest, user);

    requestHandler.handle(request);

    Response response = responseHandler.getResponse();
    assertEquals(ResponseType.E_0003.name(), response.payload().getCode());
    assertEquals("Payload Validation Error", response.payload().getTitle());
    assertEquals("Scheduling is not possible", response.payload().getDetails());
    assertNull(response.payload().getData());
  }

  @Test
  void validRemindRequestCanBeAddedToSpecificTimePoint() throws ValidationException, IOException {
    AvroRequest avroRequest = new RemindRequestBuilder().withType(HandlerType.ADD).build();
    Request request = Request.createFrom(avroRequest, user);

    requestHandler.handle(request);

    Response response = responseHandler.getResponse();
    assertEquals(ResponseType.OK.name(), response.payload().getCode());
    assertEquals("Success", response.payload().getTitle());
    assertNull(response.payload().getDetails());

    assertEquals(0, new TimePoint(60).compareTo(remindStore.getSetTimePoint()));
    assertNotNull(remindStore.getSetRemindRequest());
    assertEquals("token", remindStore.getGivenToken().value());
  }

  private static class MockRemindStore implements RemindStore {
    private TimePoint setTimePoint;
    private RemindRequest setRemindRequest;
    private Token givenToken;

    @Override
    public void setTokenBuilder(TokenBuilder tokenBuilder) {}

    @Override
    public Token set(TimePoint timePoint, RemindRequest remindRequest) {
      this.setTimePoint = timePoint;
      this.setRemindRequest = remindRequest;
      this.givenToken = new Token("token");
      return this.givenToken;
    }

    @Override
    public RemindRequest get(Token token) {
      return null;
    }

    @Override
    public boolean delete(Token token) {
      return false;
    }

    @Override
    public boolean delete(TimePoint timePoint) {
      return false;
    }

    @Override
    public Iterator<RemindRequest> iterator(
        TimePointRange timePointRange, Predicate<RemindRequest> filter) {
      return null;
    }

    public TimePoint getSetTimePoint() {
      return this.setTimePoint;
    }

    public RemindRequest getSetRemindRequest() {
      return this.setRemindRequest;
    }

    public Token getGivenToken() {
      return this.givenToken;
    }
  }
}
