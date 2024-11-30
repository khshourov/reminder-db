package com.github.khshourov.reminderdb.avro;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    // Request.payload should be null if not set
    assertNull(AvroRequest.newBuilder().setType(VALID_TYPE).build().getPayload());
  }
}
