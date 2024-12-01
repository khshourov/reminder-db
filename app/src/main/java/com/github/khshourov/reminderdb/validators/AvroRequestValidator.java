package com.github.khshourov.reminderdb.validators;

import com.github.khshourov.reminderdb.avro.AvroRequest;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.interfaces.Validator;

public class AvroRequestValidator implements Validator<AvroRequest> {
  @Override
  public void validate(AvroRequest avroRequest) throws ValidationException {
    if (avroRequest == null) {
      throw new ValidationException("avroRequest can not be null");
    }

    if (avroRequest.getType() < 0 || avroRequest.getType() > 255) {
      throw new ValidationException("type should be between 0 and 255");
    }
  }
}
