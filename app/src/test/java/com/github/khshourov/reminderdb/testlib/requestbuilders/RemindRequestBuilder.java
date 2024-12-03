package com.github.khshourov.reminderdb.testlib.requestbuilders;

import com.github.khshourov.reminderdb.avro.AvroRemindRequest;
import com.github.khshourov.reminderdb.avro.AvroRequest;
import com.github.khshourov.reminderdb.avro.AvroSchedule;
import com.github.khshourov.reminderdb.engine.requesthandlers.HandlerType;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class RemindRequestBuilder {
  private int type = HandlerType.ADD.ordinal();
  private String scheduleExpression = "0 * * * * ?";
  private String token;
  private int priority = 1;

  public RemindRequestBuilder withType(HandlerType type) {
    this.type = type.ordinal();
    return this;
  }

  public RemindRequestBuilder withScheduleExpression(String scheduleExpression) {
    this.scheduleExpression = scheduleExpression;
    return this;
  }

  public RemindRequestBuilder withToken(String token) {
    this.token = token;
    return this;
  }

  public RemindRequestBuilder withPriority(int priority) {
    this.priority = priority;
    return this;
  }

  public AvroRequest build() throws IOException {
    AvroSchedule schedule =
        AvroSchedule.newBuilder().setExpression(this.scheduleExpression).build();
    AvroRemindRequest avroRemindRequest =
        AvroRemindRequest.newBuilder()
            .setContext(ByteBuffer.allocate(1))
            .setSchedules(List.of(schedule))
            .setToken(this.token)
            .setPriority(this.priority)
            .build();

    return AvroRequest.newBuilder()
        .setType(this.type)
        .setPayload(avroRemindRequest.toByteBuffer())
        .build();
  }
}
