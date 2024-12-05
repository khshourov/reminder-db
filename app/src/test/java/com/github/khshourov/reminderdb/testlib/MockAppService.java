package com.github.khshourov.reminderdb.testlib;

import com.github.khshourov.reminderdb.engine.responsehandlers.ResponseHandler;
import com.github.khshourov.reminderdb.interfaces.AppService;
import com.github.khshourov.reminderdb.interfaces.TimeService;
import com.github.khshourov.reminderdb.lib.remindstore.RemindStore;
import com.github.khshourov.reminderdb.lib.tokenbuilder.TokenBuilder;
import com.github.khshourov.reminderdb.lib.utils.TimePointRange;
import com.github.khshourov.reminderdb.models.RemindRequest;
import com.github.khshourov.reminderdb.models.Response;
import com.github.khshourov.reminderdb.models.TimePoint;
import com.github.khshourov.reminderdb.models.Token;
import java.util.Iterator;
import java.util.function.Predicate;

public class MockAppService implements AppService {
  private final RemindStore remindStore;
  private final ResponseHandler responseHandler;
  private final TimeService timeService;

  public MockAppService(
      RemindStore remindStore, ResponseHandler responseHandler, TimeService timeService) {
    this.remindStore = remindStore;
    this.responseHandler = responseHandler;
    this.timeService = timeService;
  }

  @Override
  public void send(Response response) {
    this.responseHandler.send(response);
  }

  @Override
  public long getCurrentEpochSecond() {
    return this.timeService.getCurrentEpochSecond();
  }

  @Override
  public void setTokenBuilder(TokenBuilder tokenBuilder) {
    this.remindStore.setTokenBuilder(tokenBuilder);
  }

  @Override
  public Token set(TimePoint timePoint, RemindRequest remindRequest) {
    return this.remindStore.set(timePoint, remindRequest);
  }

  @Override
  public RemindRequest get(Token token) {
    return this.remindStore.get(token);
  }

  @Override
  public boolean delete(Token token) {
    return this.remindStore.delete(token);
  }

  @Override
  public boolean delete(TimePoint timePoint) {
    return this.remindStore.delete(timePoint);
  }

  @Override
  public Iterator<RemindRequest> iterator(
      TimePointRange timePointRange, Predicate<RemindRequest> filter) {
    return this.remindStore.iterator(timePointRange, filter);
  }
}
