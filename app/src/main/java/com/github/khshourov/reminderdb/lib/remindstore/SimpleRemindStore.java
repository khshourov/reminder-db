package com.github.khshourov.reminderdb.lib.remindstore;

import com.github.khshourov.reminderdb.lib.tokenbuilder.TokenBuilder;
import com.github.khshourov.reminderdb.lib.tokenbuilder.UuidTokenBuilder;
import com.github.khshourov.reminderdb.lib.utils.Pair;
import com.github.khshourov.reminderdb.lib.utils.TimePointRange;
import com.github.khshourov.reminderdb.lib.utils.UnsafeCircularList;
import com.github.khshourov.reminderdb.lib.utils.UnsafeNode;
import com.github.khshourov.reminderdb.models.RemindRequest;
import com.github.khshourov.reminderdb.models.TimePoint;
import com.github.khshourov.reminderdb.models.Token;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

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

  @Override
  public Iterator<RemindRequest> iterator(
      TimePointRange timePointRange, Predicate<RemindRequest> filter) {
    return new SimpleRemindStoreIterator(timePointRange, filter);
  }

  private class SimpleRemindStoreIterator implements Iterator<RemindRequest> {
    private final TimePointRange timePointRange;
    private final Predicate<RemindRequest> filter;

    private final Iterator<TimePoint> timePointIterator;
    private Iterator<RemindRequest> remindRequestIterator;
    private RemindRequest remindRequest;

    public SimpleRemindStoreIterator(
        TimePointRange timePointRange, Predicate<RemindRequest> filter) {
      this.timePointRange = timePointRange;
      this.filter = filter;

      this.timePointIterator = new ArrayList<>(remindStore.keySet()).stream().sorted().iterator();
      this.remindRequestIterator = this.nextIterator();
    }

    @Override
    public boolean hasNext() {
      this.remindRequest = this.nextRemindRequest();
      return this.remindRequest != null;
    }

    @Override
    public RemindRequest next() {
      return this.remindRequest;
    }

    private Iterator<RemindRequest> nextIterator() {
      while (this.timePointIterator.hasNext()) {
        TimePoint currTimePoint = this.timePointIterator.next();
        if (currTimePoint.isBetween(this.timePointRange)
            && remindStore.get(currTimePoint) != null) {
          return remindStore.get(currTimePoint).iterator();
        }
      }

      return null;
    }

    private RemindRequest nextRemindRequest() {
      while (this.remindRequestIterator != null) {
        while (this.remindRequestIterator.hasNext()) {
          RemindRequest remindRequest = this.remindRequestIterator.next();
          if (filter == null || filter.test(remindRequest)) {
            return remindRequest;
          }
        }

        this.remindRequestIterator = this.nextIterator();
      }

      return null;
    }
  }
}
