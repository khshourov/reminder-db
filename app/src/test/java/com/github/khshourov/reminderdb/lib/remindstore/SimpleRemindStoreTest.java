package com.github.khshourov.reminderdb.lib.remindstore;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.khshourov.reminderdb.lib.tokenbuilder.TokenBuilder;
import com.github.khshourov.reminderdb.lib.tokenbuilder.UuidTokenBuilder;
import com.github.khshourov.reminderdb.models.RemindRequest;
import com.github.khshourov.reminderdb.models.TimePoint;
import com.github.khshourov.reminderdb.models.Token;
import com.github.khshourov.reminderdb.models.User;
import java.util.Iterator;
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

  @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
  @Nested
  class WhenIterate {

    @Test
    void remindRequestsShouldBeReturnedWhenTwoTimePointsGiven() {
      User u1 = new User(1);
      User u2 = new User(2);

      RemindRequest r1 = new RemindRequest(u1);
      RemindRequest r2 = new RemindRequest(u2);
      RemindRequest r3 = new RemindRequest(u1);
      RemindRequest r4 = new RemindRequest(u2);

      TimePoint t1 = new TimePoint(1);
      TimePoint t2 = new TimePoint(2);
      TimePoint t3 = new TimePoint(3);
      TimePoint t4 = new TimePoint(4);

      Token token1 = simpleRemindStore.set(t1, r1);
      simpleRemindStore.set(t2, r2);
      Token token3 = simpleRemindStore.set(t3, r3);
      simpleRemindStore.set(t4, r4);

      TimePoint start = new TimePoint(1);
      TimePoint end = new TimePoint(4);
      Iterator<RemindRequest> iterator =
          simpleRemindStore.iterator(
              start, end, (RemindRequest r) -> r.getUser().getId() == u1.getId());

      assertTrue(iterator.hasNext());
      assertEquals(token1, iterator.next().getToken());
      assertTrue(iterator.hasNext());
      assertEquals(token3, iterator.next().getToken());
      assertFalse(iterator.hasNext());
    }

    @Test
    void timePointRangeShouldBeMonotonicallyIncreased() {
      User u1 = new User(1);
      User u2 = new User(2);

      RemindRequest r1 = new RemindRequest(u1);
      RemindRequest r2 = new RemindRequest(u2);
      RemindRequest r3 = new RemindRequest(u1);
      RemindRequest r4 = new RemindRequest(u2);

      TimePoint t1 = new TimePoint(1);
      TimePoint t2 = new TimePoint(2);
      TimePoint t3 = new TimePoint(3);
      TimePoint t4 = new TimePoint(4);

      simpleRemindStore.set(t1, r1);
      Token token2 = simpleRemindStore.set(t2, r2);
      simpleRemindStore.set(t3, r3);
      Token token4 = simpleRemindStore.set(t4, r4);

      TimePoint start = new TimePoint(5);
      TimePoint end = new TimePoint(2);
      Iterator<RemindRequest> iterator =
          simpleRemindStore.iterator(
              start, end, (RemindRequest r) -> r.getUser().getId() == u2.getId());

      assertTrue(iterator.hasNext());
      assertEquals(token2, iterator.next().getToken());
      assertTrue(iterator.hasNext());
      assertEquals(token4, iterator.next().getToken());
      assertFalse(iterator.hasNext());
    }

    @Test
    void shouldReturnAllRemindRequestsIfFilterIsNotProvided() {
      User u1 = new User(1);
      User u2 = new User(2);

      RemindRequest r1 = new RemindRequest(u1);
      RemindRequest r2 = new RemindRequest(u2);
      RemindRequest r3 = new RemindRequest(u1);
      RemindRequest r4 = new RemindRequest(u2);

      TimePoint t1 = new TimePoint(1);
      TimePoint t2 = new TimePoint(2);
      TimePoint t3 = new TimePoint(3);
      TimePoint t4 = new TimePoint(4);

      Token token1 = simpleRemindStore.set(t1, r1);
      Token token2 = simpleRemindStore.set(t2, r2);
      Token token3 = simpleRemindStore.set(t3, r3);
      Token token4 = simpleRemindStore.set(t4, r4);

      TimePoint start = new TimePoint(1);
      TimePoint end = new TimePoint(5);
      Iterator<RemindRequest> iterator = simpleRemindStore.iterator(start, end, null);

      assertTrue(iterator.hasNext());
      assertEquals(token1, iterator.next().getToken());
      assertTrue(iterator.hasNext());
      assertEquals(token2, iterator.next().getToken());
      assertTrue(iterator.hasNext());
      assertEquals(token3, iterator.next().getToken());
      assertTrue(iterator.hasNext());
      assertEquals(token4, iterator.next().getToken());
      assertFalse(iterator.hasNext());
    }

    @Test
    void startTimePointShouldSetEarliestIfNotProvided() {
      User u1 = new User(1);
      User u2 = new User(2);

      RemindRequest r1 = new RemindRequest(u1);
      RemindRequest r2 = new RemindRequest(u2);
      RemindRequest r3 = new RemindRequest(u1);
      RemindRequest r4 = new RemindRequest(u2);

      TimePoint t1 = new TimePoint(1);
      TimePoint t2 = new TimePoint(2);
      TimePoint t3 = new TimePoint(3);
      TimePoint t4 = new TimePoint(4);

      Token token1 = simpleRemindStore.set(t1, r1);
      simpleRemindStore.set(t2, r2);
      simpleRemindStore.set(t3, r3);
      simpleRemindStore.set(t4, r4);

      TimePoint end = new TimePoint(3);
      Iterator<RemindRequest> iterator =
          simpleRemindStore.iterator(
              null, end, (RemindRequest r) -> r.getUser().getId() == u1.getId());

      assertTrue(iterator.hasNext());
      assertEquals(token1, iterator.next().getToken());
      assertFalse(iterator.hasNext());
    }

    @Test
    void endTimePointShouldSetFarthestIfNotProvided() {
      User u1 = new User(1);
      User u2 = new User(2);

      RemindRequest r1 = new RemindRequest(u1);
      RemindRequest r2 = new RemindRequest(u2);
      RemindRequest r3 = new RemindRequest(u1);
      RemindRequest r4 = new RemindRequest(u2);

      TimePoint t1 = new TimePoint(1);
      TimePoint t2 = new TimePoint(2);
      TimePoint t3 = new TimePoint(3);
      TimePoint t4 = new TimePoint(4);

      simpleRemindStore.set(t1, r1);
      Token token2 = simpleRemindStore.set(t2, r2);
      simpleRemindStore.set(t3, r3);
      Token token4 = simpleRemindStore.set(t4, r4);

      TimePoint start = new TimePoint(2);
      Iterator<RemindRequest> iterator =
          simpleRemindStore.iterator(
              start, null, (RemindRequest r) -> r.getUser().getId() == u2.getId());

      assertTrue(iterator.hasNext());
      assertEquals(token2, iterator.next().getToken());
      assertTrue(iterator.hasNext());
      assertEquals(token4, iterator.next().getToken());
      assertFalse(iterator.hasNext());
    }

    @Test
    void canIterateEmptyStore() {
      User u1 = new User(1);

      TimePoint start = new TimePoint(1);
      TimePoint end = new TimePoint(4);
      Iterator<RemindRequest> iterator =
          simpleRemindStore.iterator(
              start, end, (RemindRequest r) -> r.getUser().getId() == u1.getId());

      assertFalse(iterator.hasNext());
    }

    @Test
    void rangeMayNotProvidedWithEmptyStore() {
      User u1 = new User(1);

      TimePoint start = new TimePoint(1);
      TimePoint end = new TimePoint(4);
      Iterator<RemindRequest> iterator =
          simpleRemindStore.iterator(
              null, null, (RemindRequest r) -> r.getUser().getId() == u1.getId());

      assertFalse(iterator.hasNext());
    }
  }

  private static class FixedTokenBuilder implements TokenBuilder {
    public static final String FIXED_TOKEN = "fixed-token";

    @Override
    public TokenBuilder with(Object object) {
      return this;
    }

    @Override
    public Token build() {
      return new Token(FIXED_TOKEN);
    }
  }
}
