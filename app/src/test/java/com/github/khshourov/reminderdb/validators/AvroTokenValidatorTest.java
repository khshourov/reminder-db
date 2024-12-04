package com.github.khshourov.reminderdb.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.github.khshourov.reminderdb.avro.AvroToken;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class AvroTokenValidatorTest {
  private AvroTokenValidator validator;

  @BeforeEach
  void init() {
    validator = new AvroTokenValidator();
  }

  @Test
  void avroTokenCanNotBeNull() {
    Exception exception = assertThrows(ValidationException.class, () -> validator.validate(null));

    assertEquals("avroToken can not be null", exception.getMessage());
  }

  @ParameterizedTest
  @MethodSource("invalidTokenValues")
  void avroTokenValueCanNotBeEmpty(String invalidTokenValue) {
    AvroToken avroToken = AvroToken.newBuilder().setToken(invalidTokenValue).build();

    Exception exception =
        assertThrows(ValidationException.class, () -> validator.validate(avroToken));

    assertEquals("token can not be empty", exception.getMessage());
  }

  static Stream<Arguments> invalidTokenValues() {
    return Stream.of(arguments(""), arguments(" "), arguments("  "));
  }
}
