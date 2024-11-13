package com.github.khshourov.reminderdb.lib.remindstore;

import com.github.khshourov.reminderdb.lib.tokenbuilder.TokenBuilder;
import com.github.khshourov.reminderdb.models.RemindRequest;
import com.github.khshourov.reminderdb.models.TimePoint;
import com.github.khshourov.reminderdb.models.Token;

public interface RemindStore {
  void setTokenBuilder(TokenBuilder tokenBuilder);

  Token set(TimePoint timePoint, RemindRequest remindRequest);

  RemindRequest get(Token token);

  boolean delete(Token token);

  boolean delete(TimePoint timePoint);
}
