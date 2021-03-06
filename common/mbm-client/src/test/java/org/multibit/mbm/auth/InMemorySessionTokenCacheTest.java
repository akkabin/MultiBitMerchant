package org.multibit.mbm.auth;

import com.google.common.base.Optional;
import org.junit.Test;
import org.multibit.mbm.model.ClientUser;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.*;

public class InMemorySessionTokenCacheTest {

  @Test
  public void testGetBySessionToken() throws Exception {

    ClientUser expectedClientUser = createClientUser();

    InMemorySessionTokenCache
      .INSTANCE
      .put(
        expectedClientUser.getSessionToken(),
        expectedClientUser);

    Optional<ClientUser> clientUserOptional = InMemorySessionTokenCache
      .INSTANCE
      .getBySessionToken(Optional.of(expectedClientUser.getSessionToken()));

    assertTrue(clientUserOptional.isPresent());

    assertEquals(expectedClientUser, clientUserOptional.get());

  }

  @Test
  public void testPut_WithExpiry() throws Exception {

    ClientUser expectedClientUser = createClientUser();

    InMemorySessionTokenCache
      .INSTANCE
      .reset(100, TimeUnit.MILLISECONDS)
      .put(
        expectedClientUser.getSessionToken(),
        expectedClientUser);

    // Allow time for expiry
    Thread.sleep(200);

    Optional<ClientUser> clientUserOptional = InMemorySessionTokenCache
      .INSTANCE
      .getBySessionToken(Optional.of(expectedClientUser.getSessionToken()));

    assertFalse(clientUserOptional.isPresent());

  }

  @Test
  public void testPut_WithActivityPreventingExpiry() throws Exception {

    int duration = 200;

    ClientUser expectedClientUser = createClientUser();

    InMemorySessionTokenCache
      .INSTANCE
      .reset(duration, TimeUnit.MILLISECONDS)
      .put(
        expectedClientUser.getSessionToken(),
        expectedClientUser);

    // Wait for a duration within the expiry time
    Thread.sleep(duration / 2);

    // This should reset the cache expiry giving the entry longer life
    Optional<ClientUser> clientUserOptional = InMemorySessionTokenCache
      .INSTANCE
      .getBySessionToken(Optional.of(expectedClientUser.getSessionToken()));

    assertTrue("Unexpected expiry of client user (early expiration)", clientUserOptional.isPresent());

    // Wait for a duration that now extends beyond the original expiry time
    Thread.sleep(duration * 3 / 4);

    // This should again reset the cache expiry giving the entry longer life
    clientUserOptional = InMemorySessionTokenCache
      .INSTANCE
      .getBySessionToken(Optional.of(expectedClientUser.getSessionToken()));

    assertTrue("Unexpected expiry of client user (refresh failed)", clientUserOptional.isPresent());

    // Wait for a duration that now extends beyond the refreshed expiry time
    Thread.sleep(duration * 2);

    // This should again reset the cache expiry giving the entry longer life
    clientUserOptional = InMemorySessionTokenCache
      .INSTANCE
      .getBySessionToken(Optional.of(expectedClientUser.getSessionToken()));

    assertFalse("Unexpected presence of client user (late expiration)",clientUserOptional.isPresent());

  }

  private ClientUser createClientUser() {

    ClientUser clientUser = new ClientUser();

    clientUser.setApiKey("apiKey");
    clientUser.setUsername("username");
    clientUser.setCachedAuthorities(new Authority[]{Authority.ROLE_CUSTOMER});
    clientUser.setPasswordDigest("passwordDigest");
    clientUser.setSecretKey("secretKey");
    clientUser.setSessionToken(UUID.randomUUID());

    return clientUser;
  }
}
