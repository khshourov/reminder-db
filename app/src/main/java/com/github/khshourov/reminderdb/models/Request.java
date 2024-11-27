package com.github.khshourov.reminderdb.models;

import com.github.khshourov.reminderdb.avro.AvroRequest;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.interfaces.Validator;

public class Request extends AvroRequest implements Validator {
  @Override
  public void validate() throws ValidationException {}
}
