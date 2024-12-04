package com.github.khshourov.reminderdb.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.github.khshourov.reminderdb.avro.AvroToken;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TokenTest {
  @Test
  void exceptionShouldBeThrownForInvalidAvroToken() {
    String invalidToken = "";
    AvroToken avroToken = AvroToken.newBuilder().setToken(invalidToken).build();

    Exception exception =
        assertThrows(ValidationException.class, () -> Token.createFrom(avroToken));
    assertEquals("token can not be empty", exception.getMessage());
  }

  @Test
  void exceptionShouldBeThrownForNullTokenString() {
    Exception exception =
        assertThrows(ValidationException.class, () -> Token.createFrom((String) null));
    assertEquals("token can not be empty", exception.getMessage());
  }

  @ParameterizedTest
  @MethodSource("invalidTokenString")
  void exceptionShouldBeThrownForInvalidTokenString(String invalidToken) {
    Exception exception =
        assertThrows(ValidationException.class, () -> Token.createFrom(invalidToken));
    assertEquals("token can not be empty", exception.getMessage());
  }

  static Stream<Arguments> invalidTokenString() {
    return Stream.of(arguments(""), arguments(" "), arguments("  "));
  }
}
