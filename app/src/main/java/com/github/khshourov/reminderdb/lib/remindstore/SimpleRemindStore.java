package com.github.khshourov.reminderdb.lib.remindstore;

import com.github.khshourov.reminderdb.lib.tokenbuilder.TokenBuilder;
import com.github.khshourov.reminderdb.lib.tokenbuilder.UuidTokenBuilder;
import com.github.khshourov.reminderdb.lib.utils.Pair;
import com.github.khshourov.reminderdb.lib.utils.UnsafeCircularList;
import com.github.khshourov.reminderdb.lib.utils.UnsafeNode;
import com.github.khshourov.reminderdb.models.RemindRequest;
import com.github.khshourov.reminderdb.models.TimePoint;
import com.github.khshourov.reminderdb.models.Token;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class SimpleRemindStore implements RemindStore {
  private TokenBuilder tokenBuilder;
  private final Map<TimePoint, UnsafeCircularList<RemindRequest>> remindStore;
  private final Map<Token, Pair<TimePoint, UnsafeNode<RemindRequest>>> indexes;

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

    this.remindStore.putIfAbsent(timePoint, new UnsafeCircularList<>());
    UnsafeNode<RemindRequest> node = this.remindStore.get(timePoint).add(remindRequest);

    Token token = this.tokenBuilder.with(remindRequest.getUser()).build();

    remindRequest.setToken(token);
    this.indexes.put(token, new Pair<>(timePoint, node));

    return token;
  }

  @Override
  public RemindRequest get(Token token) {
    Pair<TimePoint, UnsafeNode<RemindRequest>> pair = this.indexes.get(token);
    if (pair == null) {
      return null;
    }

    return pair.second().getItem();
  }

  @Override
  public boolean delete(Token token) {
    Pair<TimePoint, UnsafeNode<RemindRequest>> pair = this.indexes.get(token);
    if (pair == null) {
      return false;
    }

    UnsafeCircularList<RemindRequest> list = this.remindStore.get(pair.first());
    if (list == null) {
      return false;
    }

    this.indexes.remove(token);

    return list.delete(pair.second());
  }

  @Override
  public boolean delete(TimePoint timePoint) {
    UnsafeCircularList<RemindRequest> list = this.remindStore.get(timePoint);
    if (list == null) {
      return false;
    }

    Iterator<RemindRequest> iterator = list.iterator();
    while (iterator.hasNext()) {
      RemindRequest remindRequest = iterator.next();
      this.indexes.remove(remindRequest.getToken());
    }

    list.deleteAll();
    this.remindStore.remove(timePoint);

    return true;
  }
}
