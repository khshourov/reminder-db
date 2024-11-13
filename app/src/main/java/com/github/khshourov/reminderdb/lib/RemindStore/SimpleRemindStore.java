package com.github.khshourov.reminderdb.lib.RemindStore;

import com.github.khshourov.reminderdb.lib.TokenBuilder.TokenBuilder;
import com.github.khshourov.reminderdb.lib.TokenBuilder.UuidTokenBuilder;
import com.github.khshourov.reminderdb.lib.utils.Pair;
import com.github.khshourov.reminderdb.models.RemindRequest;
import com.github.khshourov.reminderdb.models.TimePoint;
import com.github.khshourov.reminderdb.models.Token;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

public class SimpleRemindStore implements RemindStore {
  private TokenBuilder tokenBuilder;
  private final Map<TimePoint, LinkedList<RemindRequest>> remindStore;
  private final Map<Token, Pair<TimePoint, RemindRequest>> indexes;

  public SimpleRemindStore() {
    this.tokenBuilder = new UuidTokenBuilder();
    this.remindStore = new HashMap<>();
    this.indexes = new HashMap<>();
  }

  public void setTokenBuilder(TokenBuilder tokenBuilder) {
    this.tokenBuilder = tokenBuilder;
  }

  @Override
  public Token set(TimePoint timePoint, RemindRequest remindRequest) {
    Objects.requireNonNull(this.tokenBuilder);
    Objects.requireNonNull(remindRequest.getUser());

    this.remindStore.putIfAbsent(timePoint, new LinkedList<>());
    this.remindStore.get(timePoint).add(remindRequest);

    Token token = this.tokenBuilder.with(remindRequest.getUser()).build();

    remindRequest.setToken(token);
    this.indexes.put(token, new Pair<>(timePoint, remindRequest));

    return token;
  }

  @Override
  public RemindRequest get(Token token) {
    Pair<TimePoint, RemindRequest> pair = this.indexes.get(token);
    if (pair == null) {
      return null;
    }

    return pair.second();
  }

  @Override
  public boolean delete(Token token) {
    Pair<TimePoint, RemindRequest> pair = this.indexes.get(token);
    if (pair == null) {
      return false;
    }

    LinkedList<RemindRequest> list = this.remindStore.get(pair.first());
    if (list == null) {
      return false;
    }

    this.indexes.remove(token);

    return list.remove(pair.second());
  }

  @Override
  public boolean delete(TimePoint timePoint) {
    LinkedList<RemindRequest> list = this.remindStore.get(timePoint);
    if (list == null) {
      return false;
    }

    for (RemindRequest remindRequest : list) {
      this.indexes.remove(remindRequest.getToken());
    }
    this.remindStore.remove(timePoint);

    return true;
  }
}
