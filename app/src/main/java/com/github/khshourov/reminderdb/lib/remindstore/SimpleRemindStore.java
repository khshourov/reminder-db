package com.github.khshourov.reminderdb.lib.remindstore;

import com.github.khshourov.reminderdb.lib.tokenbuilder.TokenBuilder;
import com.github.khshourov.reminderdb.lib.tokenbuilder.UuidTokenBuilder;
import com.github.khshourov.reminderdb.lib.utils.Pair;
import com.github.khshourov.reminderdb.lib.utils.UnsafeCircularList;
import com.github.khshourov.reminderdb.lib.utils.UnsafeNode;
import com.github.khshourov.reminderdb.models.RemindRequest;
import com.github.khshourov.reminderdb.models.TimePoint;
import com.github.khshourov.reminderdb.models.Token;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
      TimePoint start, TimePoint end, Predicate<RemindRequest> filter) {
    return new SimpleRemindStoreIterator(start, end, filter);
  }

  public class SimpleRemindStoreIterator implements Iterator<RemindRequest> {
    private TimePoint start;
    private TimePoint end;
    private Predicate<RemindRequest> filter;

    private final List<TimePoint> timePoints;
    private int currTimePointIndex;
    private Iterator<RemindRequest> iterator;
    private RemindRequest remindRequest;

    public SimpleRemindStoreIterator(
        TimePoint start, TimePoint end, Predicate<RemindRequest> filter) {
      this.timePoints = new ArrayList<>(remindStore.keySet());
      Collections.sort(this.timePoints);

      if (!this.timePoints.isEmpty()) {
        this.start = start != null ? start : this.timePoints.getFirst();
        this.end = end != null ? end : new TimePoint(this.timePoints.getLast().value() + 1);
      } else {
        this.start = new TimePoint(0);
        this.end = new TimePoint(0);
      }
      if (this.start.isGreaterThanEqual(this.end)) {
        TimePoint temp = this.start;
        this.start = this.end;
        this.end = temp;
      }
      this.filter = filter;

      this.currTimePointIndex = 0;
      this.iterator = this.nextIterator();
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
      if (this.currTimePointIndex == this.timePoints.size()) {
        return null;
      }

      TimePoint currTimePoint = this.timePoints.get(this.currTimePointIndex);
      while (!currTimePoint.isGreaterThanEqual(this.start)
          || (currTimePoint.isLessThan(this.end) && remindStore.get(currTimePoint) == null)) {
        this.currTimePointIndex = this.currTimePointIndex + 1;
        if (this.currTimePointIndex == this.timePoints.size()) {
          return null;
        }

        currTimePoint = this.timePoints.get(this.currTimePointIndex);
      }

      if (!currTimePoint.isLessThan(this.end)) {
        return null;
      }

      return remindStore.get(currTimePoint).iterator();
    }

    private RemindRequest nextRemindRequest() {
      while (this.iterator != null) {
        while (this.iterator.hasNext()) {
          RemindRequest remindRequest = this.iterator.next();
          if (filter == null || filter.test(remindRequest)) {
            return remindRequest;
          }
        }

        this.currTimePointIndex = this.currTimePointIndex + 1;
        this.iterator = this.nextIterator();
      }

      return null;
    }
  }
}
