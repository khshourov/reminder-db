package com.github.khshourov.reminderdb.models;

import com.github.khshourov.reminderdb.avro.AvroRemindRequest;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.interfaces.TimeService;
import com.github.khshourov.reminderdb.validators.AvroRemindRequestValidator;
import java.nio.ByteBuffer;
import java.util.List;

public class RemindRequest {
  private static final AvroRemindRequestValidator validator = new AvroRemindRequestValidator();

  private final TimeService timeService;

  private final AvroRemindRequest avroRemindRequest;
  private final User user;

  private Token token;

  private int scheduleId;
  private final long insertAt;
  private long updateAt;
  private long nextRemindAt;
  private int retryAttempted;

  private RemindRequest(AvroRemindRequest avroRemindRequest, User user, TimeService timeService) {
    this.timeService = timeService;

    this.avroRemindRequest = avroRemindRequest;
    this.user = user;

    this.token = new Token(avroRemindRequest.getToken());
    this.insertAt = timeService.getCurrentEpochSecond();
    this.updateAt = timeService.getCurrentEpochSecond();
  }

  public static RemindRequest createFrom(
      AvroRemindRequest avroRemindRequest, User user, TimeService timeService)
      throws ValidationException {
    if (user == null) {
      throw new ValidationException("user can not be null");
    }

    validator.validate(avroRemindRequest);

    return new RemindRequest(avroRemindRequest, user, timeService);
  }

  public ByteBuffer getContext() {
    return this.avroRemindRequest.getContext();
  }

  public List<Schedule> getSchedules() {
    return this.avroRemindRequest.getSchedules().stream()
        .map(
            (avroSchedule -> {
              try {
                return Schedule.createFrom(avroSchedule);
              } catch (ValidationException e) {
                throw new RuntimeException(e);
              }
            }))
        .toList();
  }

  public Token getToken() {
    return this.token;
  }

  public int getPriority() {
    return this.avroRemindRequest.getPriority();
  }

  public User getUser() {
    return this.user;
  }

  public void setToken(Token token) {
    this.token = token;
  }

  public int getScheduleId() {
    return this.scheduleId;
  }

  public long getInsertAt() {
    return this.insertAt;
  }

  public long getUpdateAt() {
    return this.updateAt;
  }

  public long getNextRemindAt() {
    return this.nextRemindAt;
  }

  public int getRetryAttempted() {
    return this.retryAttempted;
  }
}
