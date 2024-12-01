package com.github.khshourov.reminderdb.models;

import com.github.khshourov.reminderdb.avro.AvroRemindRequest;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.validators.AvroRemindRequestValidator;
import java.nio.ByteBuffer;
import java.util.List;

public class RemindRequest {
  private static final AvroRemindRequestValidator validator = new AvroRemindRequestValidator();
  private final AvroRemindRequest avroRemindRequest;
  private final User user;
  private Token token;

  private int scheduleId;
  private final int insertAt;
  private int nextRemindAt;
  private int retryAttempted;

  private RemindRequest(AvroRemindRequest avroRemindRequest, User user) {
    this.avroRemindRequest = avroRemindRequest;
    this.user = user;

    this.token = new Token(avroRemindRequest.getToken());
    this.insertAt = 0;
  }

  private RemindRequest(AvroRemindRequest avroRemindRequest, User user, int insertAt) {
    this.avroRemindRequest = avroRemindRequest;
    this.user = user;

    this.token = new Token(avroRemindRequest.getToken());
    this.insertAt = insertAt;
  }

  public static RemindRequest createFrom(AvroRemindRequest avroRemindRequest, User user)
      throws ValidationException {
    if (user == null) {
      throw new ValidationException("user can not be null");
    }

    validator.validate(avroRemindRequest);

    return new RemindRequest(avroRemindRequest, user);
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

  public int getInsertAt() {
    return this.insertAt;
  }

  public int getNextRemindAt() {
    return this.nextRemindAt;
  }

  public int getRetryAttempted() {
    return this.retryAttempted;
  }
}
