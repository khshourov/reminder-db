package com.github.khshourov.reminderdb;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SimpleRemindStoreTest {
  RemindStore simpleRemindStore;
  User user;

  @BeforeEach
  void createSimpleRemindStore() {
    simpleRemindStore = new SimpleRemindStore();
    user = new User(1);
  }

  @Test
  void remindStoreShouldProvideDefaultTokenBuilder() {
    TimePoint timePoint = new TimePoint(120);
    RemindRequest request = new RemindRequest(user);

    assertDoesNotThrow(
        () -> {
          this.simpleRemindStore.set(timePoint, request);
        });
  }

  @Test
  void setTokenBuilderMethodShouldUpdateTheTokenBuilder() {
    TokenBuilder tokenBuilder = new FixedTokenBuilder();
    TimePoint timePoint = new TimePoint(120);
    RemindRequest request = new RemindRequest(user);

    this.simpleRemindStore.setTokenBuilder(tokenBuilder);

    Token token = this.simpleRemindStore.set(timePoint, request);

    assertEquals(new Token(FixedTokenBuilder.FIXED_TOKEN), token);
  }

  @Test
  void canAddRequestToTimePoint() {
    TimePoint timePoint = new TimePoint(120);
    RemindRequest request = new RemindRequest(user);

    Token token = this.simpleRemindStore.set(timePoint, request);

    assertNotNull(token);
    assertFalse(token.value().isEmpty());
  }

  @Test
  void twoDifferentRequestShouldCreateTwoDifferentToken() {
    TimePoint timePoint = new TimePoint(120);

    RemindRequest request1 = new RemindRequest(user);
    RemindRequest request2 = new RemindRequest(user);

    Token token1 = this.simpleRemindStore.set(timePoint, request1);
    Token token2 = this.simpleRemindStore.set(timePoint, request2);

    assertNotEquals(token1, token2);
  }

  @Test
  void canGetPreviouslyStoredRemindRequestIfTokenProvided() {
    TimePoint timePoint = new TimePoint(120);
    RemindRequest request = new RemindRequest(user);

    Token token = this.simpleRemindStore.set(timePoint, request);

    RemindRequest storedRequest = this.simpleRemindStore.get(token);

    assertEquals(request, storedRequest);
  }

  @Test
  void shouldReturnNullIfTokenIsNotFound() {
    Token token = (new UuidTokenBuilder()).with(user).build();

    RemindRequest storedRequest = this.simpleRemindStore.get(token);

    assertNull(storedRequest);
  }
}
