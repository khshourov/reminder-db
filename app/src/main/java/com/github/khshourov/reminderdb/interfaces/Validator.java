package com.github.khshourov.reminderdb.interfaces;

import com.github.khshourov.reminderdb.exceptions.ValidationException;

public interface Validator {
  public void validate() throws ValidationException;
}
