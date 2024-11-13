package com.github.khshourov.reminderdb;

import com.github.khshourov.reminderdb.lib.TokenBuilder.TokenBuilder;

public interface RemindStore {
  void setTokenBuilder(TokenBuilder tokenBuilder);

  Token set(TimePoint timePoint, RemindRequest remindRequest);

  RemindRequest get(Token token);

  boolean delete(Token token);

  boolean delete(TimePoint timePoint);
}
