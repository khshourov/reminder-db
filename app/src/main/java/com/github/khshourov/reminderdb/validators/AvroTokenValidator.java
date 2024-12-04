package com.github.khshourov.reminderdb.validators;

import com.github.khshourov.reminderdb.avro.AvroToken;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.interfaces.Validator;

public class AvroTokenValidator implements Validator<AvroToken> {
  @Override
  public void validate(AvroToken avroToken) throws ValidationException {
    if (avroToken == null) {
      throw new ValidationException("avroToken can not be null");
    }

    if (avroToken.getToken().trim().isEmpty()) {
      throw new ValidationException("token can not be empty");
    }
  }
}
