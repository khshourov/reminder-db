package com.github.khshourov.reminderdb.validators;

import com.github.khshourov.reminderdb.avro.AvroRemindRequest;
import com.github.khshourov.reminderdb.avro.AvroSchedule;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.interfaces.Validator;

public class AvroRemindRequestValidator implements Validator<AvroRemindRequest> {
  private AvroScheduleValidator avroScheduleValidator = new AvroScheduleValidator();

  @Override
  public void validate(AvroRemindRequest avroRemindRequest) throws ValidationException {
    if (avroRemindRequest == null) {
      throw new ValidationException("avroRemindRequest can not be null");
    }

    if (avroRemindRequest.getContext().capacity() < 1) {
      throw new ValidationException("context length should be greater or equal than 1");
    }

    if (avroRemindRequest.getSchedules().isEmpty()) {
      throw new ValidationException("schedules length should be greater or equal than 1");
    }

    for (AvroSchedule avroSchedule : avroRemindRequest.getSchedules()) {
      avroScheduleValidator.validate(avroSchedule);
    }

    if (avroRemindRequest.getToken() != null && avroRemindRequest.getToken().trim().isEmpty()) {
      throw new ValidationException("token length should be greater or equal than 1");
    }

    if (avroRemindRequest.getPriority() < 1 || avroRemindRequest.getPriority() > 255) {
      throw new ValidationException("priority should be between 1 and 255");
    }
  }
}
