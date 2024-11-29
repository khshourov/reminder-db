package com.github.khshourov.reminderdb.interfaces;

import com.github.khshourov.reminderdb.exceptions.ValidationException;

public interface Validator<T> {
  public void validate(T object) throws ValidationException;
}
