package com.github.khshourov.reminderdb.lib.tokenbuilder;

import com.github.khshourov.reminderdb.models.Token;
import com.github.khshourov.reminderdb.models.User;
import java.util.Objects;
import java.util.UUID;

public class UuidTokenBuilder implements TokenBuilder {
  private User user;

  @Override
  public TokenBuilder with(Object object) {
    this.user = (User) object;
    return this;
  }

  @Override
  public Token build() {
    Objects.requireNonNull(this.user);

    return new Token(String.format("user-%d:%s", this.user.getId(), UUID.randomUUID()));
  }
}
