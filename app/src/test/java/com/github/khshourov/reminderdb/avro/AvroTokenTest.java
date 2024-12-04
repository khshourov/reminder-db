package com.github.khshourov.reminderdb.avro;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.avro.AvroRuntimeException;
import org.junit.jupiter.api.Test;

public class AvroTokenTest {
  @Test
  void tokenCanNotBeNull() {
    assertThrows(AvroRuntimeException.class, () -> AvroToken.newBuilder().setToken(null).build());
  }
}
