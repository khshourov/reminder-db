package com.github.khshourov.reminderdb.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.khshourov.reminderdb.avro.AvroRequest;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;

public class RequestTest {
  private static final Integer VALID_TYPE = 1;
  private static final ByteBuffer VALID_PAYLOAD = ByteBuffer.wrap("payload".getBytes());
  private static final Integer INVALID_TYPE = -1;

  @Test
  void invalidAvroRequestShouldThrowException() {
    AvroRequest avroRequest =
        AvroRequest.newBuilder().setType(INVALID_TYPE).setPayload(VALID_PAYLOAD).build();

    assertThrows(ValidationException.class, () -> Request.createFrom(avroRequest, null));
  }

  @Test
  void userShouldNotBeNull() {
    AvroRequest avroRequest =
        AvroRequest.newBuilder().setType(VALID_TYPE).setPayload(VALID_PAYLOAD).build();

    Exception exception =
        assertThrows(ValidationException.class, () -> Request.createFrom(avroRequest, null));

    assertEquals("user can not be null", exception.getMessage());
  }

  @Test
  void accessorMethodsShouldRetrieveCorrectValue() throws ValidationException {
    AvroRequest avroRequest =
        AvroRequest.newBuilder().setType(VALID_TYPE).setPayload(VALID_PAYLOAD).build();
    User user = new User(1);

    Request request = Request.createFrom(avroRequest, user);

    assertEquals(VALID_TYPE, request.getType());
    assertEquals(VALID_PAYLOAD, request.getPayload());
    assertEquals(user, request.getUser());
  }
}
