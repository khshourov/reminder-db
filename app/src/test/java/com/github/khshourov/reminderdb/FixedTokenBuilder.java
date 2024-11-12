package com.github.khshourov.reminderdb;

public class FixedTokenBuilder implements TokenBuilder {
  public static final String FIXED_TOKEN = "fixed-token";

  @Override
  public TokenBuilder with(Object object) {
    return this;
  }

  @Override
  public Token build() {
    return new Token(FIXED_TOKEN);
  }
}
