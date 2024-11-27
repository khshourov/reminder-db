package com.github.khshourov.reminderdb.models;

import com.github.khshourov.reminderdb.avro.AvroRequest;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.interfaces.Validator;

public record Request(AvroRequest avroRequest) implements Validator {

  public Request {
    if (avroRequest == null) {
      throw new IllegalArgumentException("avroRequest can not be null");
    }
  }

  @Override
  public void validate() throws ValidationException {
    if (this.avroRequest.getType() < 0 || this.avroRequest.getType() > 255) {
      throw new ValidationException("type should be between 0 and 255");
    }

    if (this.avroRequest.getClientId().trim().isEmpty()) {
      throw new ValidationException("clientId should not be empty");
    }
  }
}
