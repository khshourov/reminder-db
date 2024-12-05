package com.github.khshourov.reminderdb.engine.requesthandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.github.khshourov.reminderdb.avro.AvroRequest;
import com.github.khshourov.reminderdb.avro.AvroToken;
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
import com.github.khshourov.reminderdb.testlib.MockAppService;
import com.github.khshourov.reminderdb.testlib.MockRequestMultiplexer;
import com.github.khshourov.reminderdb.testlib.MockResponseHandler;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeleteRequestHandlerTest {
  private static final String VALID_TOKEN = "valid-token";

  private DeleteRequestHandler deleteRequestHandler;
  private MockRequestMultiplexer requestMultiplexer;
  private User user;
  private MockResponseHandler responseHandler;
  private MockRemindStore remindStore;

  @BeforeEach
  void init() {
    responseHandler = new MockResponseHandler();
    remindStore = new MockRemindStore();
    deleteRequestHandler =
        new DeleteRequestHandler(new MockAppService(remindStore, responseHandler, null));
    requestMultiplexer = new MockRequestMultiplexer();
    user = new User(1);
  }

  @Test
  void handlerIdShouldBeOfTypeDelete() {
    assertEquals(HandlerType.DELETE.ordinal(), deleteRequestHandler.handlerId());
  }

  @Test
  void requestHandlerShouldRegisterItselfToRequestMultiplexer() {
    deleteRequestHandler.register(requestMultiplexer);

    assertEquals(
        deleteRequestHandler,
        requestMultiplexer.getRequestHandler(deleteRequestHandler.handlerId()));
  }

  @Test
  void requestTypeShouldBeDeleteRequest() throws ValidationException {
    AvroRequest avroRequest =
        AvroRequest.newBuilder()
            .setType(HandlerType.ADD.ordinal())
            .setPayload(ByteBuffer.allocate(1))
            .build();
    Request request = Request.createFrom(avroRequest, user);

    deleteRequestHandler.handle(request);

    Response response = responseHandler.getResponse();
    assertEquals(ResponseType.E_0001.name(), response.payload().getCode());
    assertEquals("Internal Error", response.payload().getTitle());
    assertNull(response.payload().getData());
  }

  @Test
  void payloadShouldBeValidAvroRequest() throws ValidationException, IOException {
    AvroRequest avroRequest =
        AvroRequest.newBuilder()
            .setType(HandlerType.DELETE.ordinal())
            .setPayload(ByteBuffer.allocate(1))
            .build();
    Request request = Request.createFrom(avroRequest, user);

    deleteRequestHandler.handle(request);

    Response response = responseHandler.getResponse();
    assertEquals(ResponseType.E_0002.name(), response.payload().getCode());
    assertEquals("Payload Parsing Error", response.payload().getTitle());
    assertEquals("Not enough header bytes", response.payload().getDetails());
    assertNull(response.payload().getData());
  }

  @Test
  void payloadShouldBeValidDeleteRequest() throws ValidationException, IOException {
    AvroRequest avroRequest =
        AvroRequest.newBuilder()
            .setType(HandlerType.DELETE.ordinal())
            .setPayload(AvroToken.newBuilder().setToken("").build().toByteBuffer())
            .build();
    Request request = Request.createFrom(avroRequest, user);

    deleteRequestHandler.handle(request);

    Response response = responseHandler.getResponse();
    assertEquals(ResponseType.E_0003.name(), response.payload().getCode());
    assertEquals("Payload Validation Error", response.payload().getTitle());
    assertEquals("token can not be empty", response.payload().getDetails());
    assertNull(response.payload().getData());
  }

  @Test
  void deletableTokenShouldBeInRemindStore() throws IOException, ValidationException {
    Token expectedToken = Token.createFrom(VALID_TOKEN);
    AvroRequest avroRequest =
        AvroRequest.newBuilder()
            .setType(HandlerType.DELETE.ordinal())
            .setPayload(
                AvroToken.newBuilder().setToken(expectedToken.getValue()).build().toByteBuffer())
            .build();
    Request request = Request.createFrom(avroRequest, user);
    remindStore.setDeletable(false);

    deleteRequestHandler.handle(request);

    Response response = responseHandler.getResponse();
    assertEquals(ResponseType.E_0003.name(), response.payload().getCode());
    assertEquals("Payload Validation Error", response.payload().getTitle());
    assertEquals("invalid token", response.payload().getDetails());
    assertNull(response.payload().getData());

    assertEquals(expectedToken, remindStore.getDeletedToken());
  }

  @Test
  void validTokenShouldBeDeletableFromRemindStore() throws IOException, ValidationException {
    Token expectedToken = Token.createFrom(VALID_TOKEN);
    AvroRequest avroRequest =
        AvroRequest.newBuilder()
            .setType(HandlerType.DELETE.ordinal())
            .setPayload(
                AvroToken.newBuilder().setToken(expectedToken.getValue()).build().toByteBuffer())
            .build();
    Request request = Request.createFrom(avroRequest, user);
    remindStore.setDeletable(true);

    deleteRequestHandler.handle(request);

    Response response = responseHandler.getResponse();
    assertEquals(ResponseType.OK.name(), response.payload().getCode());
    assertEquals("Success", response.payload().getTitle());
    assertNull(response.payload().getDetails());
    assertNull(response.payload().getData());

    assertEquals(expectedToken, remindStore.getDeletedToken());
  }

  private static class MockRemindStore implements RemindStore {
    private Token deletedToken;
    private boolean deleted;

    @Override
    public void setTokenBuilder(TokenBuilder tokenBuilder) {}

    @Override
    public Token set(TimePoint timePoint, RemindRequest remindRequest) {
      return null;
    }

    @Override
    public RemindRequest get(Token token) {
      return null;
    }

    @Override
    public boolean delete(Token token) {
      this.deletedToken = token;

      return this.deleted;
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

    public void setDeletable(boolean deletable) {
      this.deleted = deletable;
    }

    public Token getDeletedToken() {
      return this.deletedToken;
    }
  }
}
