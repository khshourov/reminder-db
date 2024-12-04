package com.github.khshourov.reminderdb.models;

import com.github.khshourov.reminderdb.avro.AvroRemindRequest;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.interfaces.TimeService;
import com.github.khshourov.reminderdb.validators.AvroRemindRequestValidator;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

public class RemindRequest {
  private static final AvroRemindRequestValidator validator = new AvroRemindRequestValidator();

  private final TimeService timeService;

  private final AvroRemindRequest avroRemindRequest;
  private final User user;

  private Token token;

  private final List<Schedule> schedules;
  private int scheduleId;
  private final long insertAt;
  private final long updateAt;
  private long nextRemindAt;
  private int retryAttempted;

  private RemindRequest(AvroRemindRequest avroRemindRequest, User user, TimeService timeService) {
    this.timeService = timeService;

    this.avroRemindRequest = avroRemindRequest;
    this.user = user;

    this.schedules =
        avroRemindRequest.getSchedules().stream()
            .map(
                (avroSchedule -> {
                  try {
                    return Schedule.createFrom(avroSchedule);
                  } catch (ValidationException e) {
                    throw new RuntimeException(e);
                  }
                }))
            .toList();
    this.insertAt = timeService.getCurrentEpochSecond();
    this.updateAt = timeService.getCurrentEpochSecond();
    this.nextRemindAt = timeService.getCurrentEpochSecond();
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
    return this.schedules;
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

  public long getInsertAt() {
    return this.insertAt;
  }

  public long getUpdateAt() {
    return this.updateAt;
  }

  public Optional<Long> refreshNextRemindAt() {
    while (this.scheduleId < this.schedules.size()) {
      Optional<Long> nextSchedule =
          this.schedules
              .get(this.scheduleId)
              .getNextSchedule(this.nextRemindAt, this.timeService.getCurrentEpochSecond());
      if (nextSchedule.isPresent()) {
        this.nextRemindAt = nextSchedule.get();
        return Optional.of(this.nextRemindAt);
      }

      this.scheduleId = this.scheduleId + 1;
    }

    return Optional.empty();
  }

  public long getNextRemindAt() {
    return this.nextRemindAt;
  }

  public boolean canRetry(int maxRetry) {
    if ((this.retryAttempted + 1) > maxRetry) {
      return false;
    }

    this.retryAttempted = this.retryAttempted + 1;
    return true;
  }

  public int getRetryAttempted() {
    return this.retryAttempted;
  }

  public void clearRetryAttempted() {
    this.retryAttempted = 0;
  }
}
