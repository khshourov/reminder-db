package com.github.khshourov.reminderdb.models;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.github.khshourov.reminderdb.avro.AvroRequest;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import java.nio.ByteBuffer;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class RequestTest {
  private static final Integer VALID_TYPE = 1;
  private static final ByteBuffer VALID_PAYLOAD = ByteBuffer.wrap("payload".getBytes());
  private static final User VALID_USER = new User(1);

  @Test
  void avroRequestShouldNotBeNull() {
    assertThrows(IllegalArgumentException.class, () -> new Request(null, VALID_USER));
  }

  @Test
  void userShouldNotBeNull() {
    AvroRequest avroRequest =
        AvroRequest.newBuilder().setType(VALID_TYPE).setPayload(VALID_PAYLOAD).build();

    assertThrows(IllegalArgumentException.class, () -> new Request(avroRequest, null));
  }

  @ParameterizedTest
  @MethodSource("validRequestType")
  void requestTypeShouldBeBetween0And255(int requestType) {
    AvroRequest avroRequest =
        AvroRequest.newBuilder().setType(requestType).setPayload(VALID_PAYLOAD).build();
    Request request = new Request(avroRequest, VALID_USER);

    assertDoesNotThrow(request::validate);
  }

  static Stream<Arguments> validRequestType() {
    return Stream.of(arguments(0), arguments(1), arguments(128), arguments(255));
  }

  @ParameterizedTest
  @MethodSource("invalidRequestType")
  void validateMethodShouldThrowErrorWhenTypeIsNotBetween0And255(int invalidRequestType) {
    AvroRequest avroRequest =
        AvroRequest.newBuilder().setType(invalidRequestType).setPayload(VALID_PAYLOAD).build();
    Request request = new Request(avroRequest, VALID_USER);

    assertThrows(ValidationException.class, request::validate);
  }

  static Stream<Arguments> invalidRequestType() {
    return Stream.of(arguments(-1), arguments(256));
  }

  @Test
  void payloadCanBeNull() {
    AvroRequest avroRequest = AvroRequest.newBuilder().setType(VALID_TYPE).setPayload(null).build();
    Request request = new Request(avroRequest, VALID_USER);

    assertDoesNotThrow(request::validate);
  }

  @Test
  void storedAvroRequestCanBeRetrieved() {
    AvroRequest avroRequest =
        AvroRequest.newBuilder().setType(VALID_TYPE).setPayload(VALID_PAYLOAD).build();
    Request request = new Request(avroRequest, VALID_USER);

    assertEquals(avroRequest, request.avroRequest());
  }
}
