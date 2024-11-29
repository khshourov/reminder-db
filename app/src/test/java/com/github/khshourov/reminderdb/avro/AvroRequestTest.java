package com.github.khshourov.reminderdb.avro;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class AvroRequestTest {
  private static final Integer VALID_TYPE = 1;

  @Test
  void payloadCanBeNull() {
    assertDoesNotThrow(() -> AvroRequest.newBuilder().setType(VALID_TYPE).setPayload(null).build());
  }

  @Test
  void settingPayloadCanBeSkipped() {
    assertDoesNotThrow(() -> AvroRequest.newBuilder().setType(VALID_TYPE).build());
  }
}
