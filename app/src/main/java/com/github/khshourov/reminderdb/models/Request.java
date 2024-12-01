package com.github.khshourov.reminderdb.models;

import com.github.khshourov.reminderdb.avro.AvroRequest;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.validators.AvroRequestValidator;
import java.nio.ByteBuffer;

public class Request {
  private static final AvroRequestValidator validator = new AvroRequestValidator();
  private final AvroRequest avroRequest;
  private final User user;

  private Request(AvroRequest avroRequest, User user) {
    this.avroRequest = avroRequest;
    this.user = user;
  }

  public static Request createFrom(AvroRequest avroRequest, User user) throws ValidationException {
    validator.validate(avroRequest);

    if (user == null) {
      throw new ValidationException("user can not be null");
    }

    return new Request(avroRequest, user);
  }

  public int getType() {
    return this.avroRequest.getType();
  }

  public ByteBuffer getPayload() {
    return this.avroRequest.getPayload();
  }

  public User getUser() {
    return this.user;
  }
}
