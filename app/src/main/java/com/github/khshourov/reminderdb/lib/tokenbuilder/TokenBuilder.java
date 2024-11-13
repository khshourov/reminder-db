package com.github.khshourov.reminderdb.lib.tokenbuilder;

import com.github.khshourov.reminderdb.models.Token;

public interface TokenBuilder {
  TokenBuilder with(Object object);

  Token build();
}
