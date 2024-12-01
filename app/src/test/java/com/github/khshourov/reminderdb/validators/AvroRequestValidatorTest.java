package com.github.khshourov.reminderdb.validators;

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

public class AvroRequestValidatorTest {
  private static final ByteBuffer VALID_PAYLOAD = ByteBuffer.wrap("payload".getBytes());

  private final AvroRequestValidator requestValidator = new AvroRequestValidator();

  @Test
  void avroRequestShouldNotBeNull() {
    Exception exception =
        assertThrows(ValidationException.class, () -> requestValidator.validate(null));

    assertEquals("avroRequest can not be null", exception.getMessage());
  }

  @ParameterizedTest
  @MethodSource("validRequestType")
  void requestTypeShouldBeBetween0And255(int requestType) {
    AvroRequest avroRequest =
        AvroRequest.newBuilder().setType(requestType).setPayload(VALID_PAYLOAD).build();

    assertDoesNotThrow(() -> requestValidator.validate(avroRequest));
  }

  static Stream<Arguments> validRequestType() {
    return Stream.of(arguments(0), arguments(1), arguments(128), arguments(255));
  }

  @ParameterizedTest
  @MethodSource("invalidRequestType")
  void validateMethodShouldThrowErrorWhenTypeIsNotBetween0And255(int invalidRequestType) {
    AvroRequest avroRequest =
        AvroRequest.newBuilder().setType(invalidRequestType).setPayload(VALID_PAYLOAD).build();

    Exception exception =
        assertThrows(ValidationException.class, () -> requestValidator.validate(avroRequest));

    assertEquals("type should be between 0 and 255", exception.getMessage());
  }

  static Stream<Arguments> invalidRequestType() {
    return Stream.of(arguments(-1), arguments(256));
  }
}
