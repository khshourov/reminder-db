package com.github.khshourov.reminderdb.validators;

import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.interfaces.Validator;
import com.github.khshourov.reminderdb.models.Request;

public class RequestValidator implements Validator<Request> {
  @Override
  public void validate(Request request) throws ValidationException {
    if (request.avroRequest() == null) {
      throw new ValidationException("avroRequest can not be null");
    }

    if (request.user() == null) {
      throw new ValidationException("user can not be null");
    }

    if (request.avroRequest().getType() < 0 || request.avroRequest().getType() > 255) {
      throw new ValidationException("type should be between 0 and 255");
    }
  }
}
