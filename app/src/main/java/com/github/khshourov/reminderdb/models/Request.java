package com.github.khshourov.reminderdb.models;

import com.github.khshourov.reminderdb.avro.AvroRequest;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.interfaces.Validator;

public record Request(AvroRequest avroRequest, User user) implements Validator {

  public Request {
    if (avroRequest == null) {
      throw new IllegalArgumentException("avroRequest can not be null");
    }

    if (user == null) {
      throw new IllegalArgumentException("user can not be null");
    }
  }

  @Override
  public void validate() throws ValidationException {
    if (this.avroRequest.getType() < 0 || this.avroRequest.getType() > 255) {
      throw new ValidationException("type should be between 0 and 255");
    }
  }
}
