package com.github.khshourov.reminderdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UuidTokenBuilderTest {
  TokenBuilder tokenBuilder;

  @BeforeEach
  void createTokenBuilder() {
    this.tokenBuilder = new UuidTokenBuilder();
  }

  @Test
  void withMethodShouldReturnTheBuilderInstance() {
    User user = new User(1);

    assertEquals(this.tokenBuilder, this.tokenBuilder.with(user));
  }

  @Test
  void nullUserShouldThrowException() {
    assertThrows(
        NullPointerException.class,
        () -> {
          this.tokenBuilder.build();
        });
  }

  @Test
  void generateTokenThatHasUserIdPrefix() {
    User user = new User(1);

    Token token = this.tokenBuilder.with(user).build();

    assertNotNull(token);
    assertTrue(token.value().startsWith("user-1"));
  }

  @Test
  void generateTokenThatHasUuid4Suffix() {
    User user = new User(1);

    Token token = this.tokenBuilder.with(user).build();

    assertNotNull(token);
    assertEquals(2, token.value().split(":").length);

    String uuid = token.value().split(":")[1];

    assertNotNull(uuid);
    assertEquals(36, uuid.length());
    assertEquals('4', uuid.charAt(14));
  }
}
