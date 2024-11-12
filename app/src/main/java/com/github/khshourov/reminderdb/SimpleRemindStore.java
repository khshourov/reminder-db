package com.github.khshourov.reminderdb;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

public class SimpleRemindStore implements RemindStore {
  private TokenBuilder tokenBuilder;
  private final Map<TimePoint, LinkedList<RemindRequest>> remindStore;

  public SimpleRemindStore() {
    this.tokenBuilder = new UuidTokenBuilder();
    this.remindStore = new HashMap<>();
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
    return this.tokenBuilder.with(remindRequest.getUser()).build();
  }

  @Override
  public RemindRequest get(Token token) {
    return null;
  }

  @Override
  public boolean delete(Token token) {
    return false;
  }

  @Override
  public boolean delete(TimePoint timePoint) {
    return false;
  }
}
