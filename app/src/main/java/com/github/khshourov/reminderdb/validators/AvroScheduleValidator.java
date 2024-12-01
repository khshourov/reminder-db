package com.github.khshourov.reminderdb.validators;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import com.github.khshourov.reminderdb.avro.AvroSchedule;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.interfaces.Validator;

public class AvroScheduleValidator implements Validator<AvroSchedule> {
  @Override
  public void validate(AvroSchedule avroSchedule) throws ValidationException {
    if (avroSchedule == null) {
      throw new ValidationException("avroSchedule can not be null");
    }

    try {
      CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
      CronParser expressionParser = new CronParser(cronDefinition);
      Cron cron = expressionParser.parse(avroSchedule.getExpression());
      cron.validate();
    } catch (IllegalArgumentException | IllegalStateException exception) {
      throw new ValidationException(exception.getMessage());
    }

    int totalReminders = avroSchedule.getTotalReminders();
    if (totalReminders != -1 && totalReminders < 1) {
      throw new ValidationException("totalReminders should be between 1 and 2147483647 or -1");
    }
  }
}
