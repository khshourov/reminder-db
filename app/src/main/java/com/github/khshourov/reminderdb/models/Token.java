package com.github.khshourov.reminderdb.models;

import com.github.khshourov.reminderdb.avro.AvroToken;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.validators.AvroTokenValidator;

public class Token {
  private static final AvroTokenValidator validator = new AvroTokenValidator();

  private final String value;

  private Token(AvroToken avroToken) {
    this.value = avroToken.getToken();
  }

  private Token(String value) {
    this.value = value;
  }

  public static Token createFrom(AvroToken avroToken) throws ValidationException {
    validator.validate(avroToken);

    return new Token(avroToken);
  }

  public static Token createFrom(String value) throws ValidationException {
    if (value == null || value.trim().isEmpty()) {
      throw new ValidationException("token can not be empty");
    }

    return new Token(value);
  }

  public String getValue() {
    return this.value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Token other)) {
      return false;
    }

    return this.value.equals(other.value);
  }
}
