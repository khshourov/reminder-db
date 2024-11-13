package com.github.khshourov.reminderdb;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.khshourov.reminderdb.lib.TokenBuilder.FixedTokenBuilder;
import com.github.khshourov.reminderdb.lib.TokenBuilder.TokenBuilder;
import com.github.khshourov.reminderdb.lib.TokenBuilder.UuidTokenBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SimpleRemindStoreTest {
  final TimePoint timePoint120 = new TimePoint(120);
  RemindStore simpleRemindStore;
  User user;

  @BeforeEach
  void init() {
    this.simpleRemindStore = new SimpleRemindStore();
    this.user = new User(1);
  }

  @Test
  void remindStoreShouldProvideDefaultTokenBuilder() {
    RemindRequest request = new RemindRequest(user);

    assertDoesNotThrow(
        () -> {
          this.simpleRemindStore.set(timePoint120, request);
        });
  }

  @Nested
  class WhenSetTokenBuilder {
    @Test
    void setTokenBuilderMethodShouldUpdateTheTokenBuilder() {
      TokenBuilder tokenBuilder = new FixedTokenBuilder();
      RemindRequest request = new RemindRequest(user);

      simpleRemindStore.setTokenBuilder(tokenBuilder);

      Token token = simpleRemindStore.set(timePoint120, request);

      assertEquals(new Token(FixedTokenBuilder.FIXED_TOKEN), token);
    }
  }

  @Nested
  class WhenSet {
    @Test
    void remindRequestShouldBeAddedToTimePoint() {
      RemindRequest request = new RemindRequest(user);

      Token token = simpleRemindStore.set(timePoint120, request);

      assertNotNull(token);
      assertFalse(token.value().isEmpty());
    }

    @Test
    void twoDifferentRequestShouldCreateTwoDifferentToken() {
      RemindRequest request1 = new RemindRequest(user);
      RemindRequest request2 = new RemindRequest(user);

      Token token1 = simpleRemindStore.set(timePoint120, request1);
      Token token2 = simpleRemindStore.set(timePoint120, request2);

      assertNotEquals(token1, token2);
    }
  }

  @Nested
  class WhenGet {
    @Test
    void previouslyStoredRemindRequestShouldBeFetchedIfValidTokenIsProvided() {
      RemindRequest request = new RemindRequest(user);

      Token token = simpleRemindStore.set(timePoint120, request);

      RemindRequest storedRequest = simpleRemindStore.get(token);

      assertEquals(request, storedRequest);
    }

    @Test
    void nullShouldReturnIfTokenIsNotFound() {
      Token token = (new UuidTokenBuilder()).with(user).build();

      RemindRequest storedRequest = simpleRemindStore.get(token);

      assertNull(storedRequest);
    }
  }

  @Nested
  class WhenDelete {
    @Test
    void previouslyStoredRemindRequestShouldBeDeletedIfTokenIsProvided() {
      RemindRequest request = new RemindRequest(user);

      Token token = simpleRemindStore.set(timePoint120, request);

      boolean deleted = simpleRemindStore.delete(token);

      assertTrue(deleted);
      assertNull(simpleRemindStore.get(token));
    }

    @Test
    void falseShouldReturnIfTokenIsNotFound() {
      Token token = (new UuidTokenBuilder()).with(user).build();

      boolean deleted = simpleRemindStore.delete(token);

      assertFalse(deleted);
    }

    @Test
    void previouslyStoredRemindRequestsShouldBeDeletedIfTimePointIsProvided() {
      RemindRequest request1 = new RemindRequest(user);
      RemindRequest request2 = new RemindRequest(user);

      Token token1 = simpleRemindStore.set(timePoint120, request1);
      Token token2 = simpleRemindStore.set(timePoint120, request2);

      boolean deleted = simpleRemindStore.delete(timePoint120);

      assertTrue(deleted);
      assertNull(simpleRemindStore.get(token1));
      assertNull(simpleRemindStore.get(token2));
    }

    @Test
    void falseShouldReturnIfTimePointIsNotFound() {
      boolean deleted = simpleRemindStore.delete(timePoint120);

      assertFalse(deleted);
    }
  }
}
