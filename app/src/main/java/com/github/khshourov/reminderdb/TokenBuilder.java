package com.github.khshourov.reminderdb;

public interface TokenBuilder {
  TokenBuilder with(Object object);

  Token build();
}
