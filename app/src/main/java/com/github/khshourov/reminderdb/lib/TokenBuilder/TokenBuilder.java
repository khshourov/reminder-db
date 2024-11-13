package com.github.khshourov.reminderdb.lib.TokenBuilder;

import com.github.khshourov.reminderdb.Token;

public interface TokenBuilder {
  TokenBuilder with(Object object);

  Token build();
}
