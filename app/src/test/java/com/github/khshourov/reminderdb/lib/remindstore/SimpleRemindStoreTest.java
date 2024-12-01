package com.github.khshourov.reminderdb.lib.remindstore;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.khshourov.reminderdb.avro.AvroRemindRequest;
import com.github.khshourov.reminderdb.avro.AvroSchedule;
import com.github.khshourov.reminderdb.exceptions.ValidationException;
import com.github.khshourov.reminderdb.lib.tokenbuilder.TokenBuilder;
import com.github.khshourov.reminderdb.lib.tokenbuilder.UuidTokenBuilder;
import com.github.khshourov.reminderdb.lib.utils.TimePointRange;
import com.github.khshourov.reminderdb.models.RemindRequest;
import com.github.khshourov.reminderdb.models.TimePoint;
import com.github.khshourov.reminderdb.models.Token;
import com.github.khshourov.reminderdb.models.User;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SimpleRemindStoreTest {
  private static final String VALID_EXPRESSION = "0 0/5 9-17 15W 1/2 ? 2023-2025";
  private final TimePoint timePoint120 = new TimePoint(120);
  private RemindStore simpleRemindStore;
  private User user;
  private AvroRemindRequest validAvroRemindRequest;

  @BeforeEach
  void init() {
    this.simpleRemindStore = new SimpleRemindStore();
    this.user = new User(1);

    validAvroRemindRequest =
        AvroRemindRequest.newBuilder()
            .setContext(ByteBuffer.allocate(1))
            .setSchedules(
                List.of(AvroSchedule.newBuilder().setExpression(VALID_EXPRESSION).build()))
            .build();
  }

  @Test
  void remindStoreShouldProvideDefaultTokenBuilder() throws ValidationException {
    RemindRequest request = RemindRequest.createFrom(validAvroRemindRequest, user);

    assertDoesNotThrow(() -> this.simpleRemindStore.set(timePoint120, request));
  }

  @Nested
  class WhenSetTokenBuilder {
    @Test
    void setTokenBuilderMethodShouldUpdateTheTokenBuilder() throws ValidationException {
      TokenBuilder tokenBuilder = new FixedTokenBuilder();
      RemindRequest request = RemindRequest.createFrom(validAvroRemindRequest, user);

      simpleRemindStore.setTokenBuilder(tokenBuilder);

      Token token = simpleRemindStore.set(timePoint120, request);

      assertEquals(new Token(FixedTokenBuilder.FIXED_TOKEN), token);
    }
  }

  @Nested
  class WhenSet {
    @Test
    void remindRequestShouldBeAddedToTimePoint() throws ValidationException {
      RemindRequest request = RemindRequest.createFrom(validAvroRemindRequest, user);

      Token token = simpleRemindStore.set(timePoint120, request);

      assertNotNull(token);
      assertFalse(token.value().isEmpty());
    }

    @Test
    void twoDifferentRequestShouldCreateTwoDifferentToken() throws ValidationException {
      RemindRequest request1 = RemindRequest.createFrom(validAvroRemindRequest, user);
      RemindRequest request2 = RemindRequest.createFrom(validAvroRemindRequest, user);

      Token token1 = simpleRemindStore.set(timePoint120, request1);
      Token token2 = simpleRemindStore.set(timePoint120, request2);

      assertNotEquals(token1, token2);
    }
  }

  @Nested
  class WhenGet {
    @Test
    void previouslyStoredRemindRequestShouldBeFetchedIfValidTokenIsProvided()
        throws ValidationException {
      RemindRequest request = RemindRequest.createFrom(validAvroRemindRequest, user);

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
    void previouslyStoredRemindRequestShouldBeDeletedIfTokenIsProvided()
        throws ValidationException {
      RemindRequest request = RemindRequest.createFrom(validAvroRemindRequest, user);

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
    void previouslyStoredRemindRequestsShouldBeDeletedIfTimePointIsProvided()
        throws ValidationException {
      RemindRequest request1 = RemindRequest.createFrom(validAvroRemindRequest, user);
      RemindRequest request2 = RemindRequest.createFrom(validAvroRemindRequest, user);

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
    User u1 = new User(1);
    User u2 = new User(2);

    RemindRequest r1;
    RemindRequest r2;
    RemindRequest r3;
    RemindRequest r4;

    TimePoint t1 = new TimePoint(1);
    TimePoint t2 = new TimePoint(2);
    TimePoint t3 = new TimePoint(3);
    TimePoint t4 = new TimePoint(4);

    Predicate<RemindRequest> user1Filter =
        (RemindRequest remindRequest) -> remindRequest.getUser().getId() == u1.getId();
    Predicate<RemindRequest> user2Filter =
        (RemindRequest remindRequest) -> remindRequest.getUser().getId() == u2.getId();

    @BeforeEach
    void init() throws ValidationException {
      r1 = RemindRequest.createFrom(validAvroRemindRequest, u1);
      r2 = RemindRequest.createFrom(validAvroRemindRequest, u2);
      r3 = RemindRequest.createFrom(validAvroRemindRequest, u1);
      r4 = RemindRequest.createFrom(validAvroRemindRequest, u2);
    }

    @Test
    void remindRequestsShouldBeReturnedWhenTwoTimePointsGiven() {
      Token token1 = simpleRemindStore.set(t1, r1);
      simpleRemindStore.set(t2, r2);
      Token token3 = simpleRemindStore.set(t3, r3);
      simpleRemindStore.set(t4, r4);

      TimePoint start = new TimePoint(1);
      TimePoint end = new TimePoint(4);
      Iterator<RemindRequest> iterator =
          simpleRemindStore.iterator(new TimePointRange(start, end), user1Filter);

      assertTrue(iterator.hasNext());
      assertEquals(token1, iterator.next().getToken());
      assertTrue(iterator.hasNext());
      assertEquals(token3, iterator.next().getToken());
      assertFalse(iterator.hasNext());
    }

    @Test
    void timePointRangeShouldBeMonotonicallyIncreased() {
      simpleRemindStore.set(t1, r1);
      Token token2 = simpleRemindStore.set(t2, r2);
      simpleRemindStore.set(t3, r3);
      Token token4 = simpleRemindStore.set(t4, r4);

      TimePoint start = new TimePoint(5);
      TimePoint end = new TimePoint(2);
      Iterator<RemindRequest> iterator =
          simpleRemindStore.iterator(new TimePointRange(start, end), user2Filter);

      assertTrue(iterator.hasNext());
      assertEquals(token2, iterator.next().getToken());
      assertTrue(iterator.hasNext());
      assertEquals(token4, iterator.next().getToken());
      assertFalse(iterator.hasNext());
    }

    @Test
    void shouldReturnAllRemindRequestsIfFilterIsNotProvided() {
      Token token1 = simpleRemindStore.set(t1, r1);
      Token token2 = simpleRemindStore.set(t2, r2);
      Token token3 = simpleRemindStore.set(t3, r3);
      Token token4 = simpleRemindStore.set(t4, r4);

      TimePoint start = new TimePoint(1);
      TimePoint end = new TimePoint(5);
      Iterator<RemindRequest> iterator =
          simpleRemindStore.iterator(new TimePointRange(start, end), null);

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
      Token token1 = simpleRemindStore.set(t1, r1);
      simpleRemindStore.set(t2, r2);
      simpleRemindStore.set(t3, r3);
      simpleRemindStore.set(t4, r4);

      TimePoint end = new TimePoint(3);
      Iterator<RemindRequest> iterator =
          simpleRemindStore.iterator(new TimePointRange(null, end), user1Filter);

      assertTrue(iterator.hasNext());
      assertEquals(token1, iterator.next().getToken());
      assertFalse(iterator.hasNext());
    }

    @Test
    void endTimePointShouldSetFarthestIfNotProvided() {
      simpleRemindStore.set(t1, r1);
      Token token2 = simpleRemindStore.set(t2, r2);
      simpleRemindStore.set(t3, r3);
      Token token4 = simpleRemindStore.set(t4, r4);

      TimePoint start = new TimePoint(2);
      Iterator<RemindRequest> iterator =
          simpleRemindStore.iterator(new TimePointRange(start, null), user2Filter);

      assertTrue(iterator.hasNext());
      assertEquals(token2, iterator.next().getToken());
      assertTrue(iterator.hasNext());
      assertEquals(token4, iterator.next().getToken());
      assertFalse(iterator.hasNext());
    }

    @Test
    void canIterateEmptyStore() {
      TimePoint start = new TimePoint(1);
      TimePoint end = new TimePoint(4);
      Iterator<RemindRequest> iterator =
          simpleRemindStore.iterator(new TimePointRange(start, end), user1Filter);

      assertFalse(iterator.hasNext());
    }

    @Test
    void rangeMayNotProvidedWithEmptyStore() {
      Iterator<RemindRequest> iterator =
          simpleRemindStore.iterator(new TimePointRange(null, null), user1Filter);

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
