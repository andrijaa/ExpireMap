import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ExpirationThreadTest {

  @Test
  void add() {
    ExpirationThread expirationThread = new ExpirationThread(new ConcurrentExpireHashMap());
    expirationThread.addExpiration("key1", 2000);
    assertEquals(1,expirationThread.expirationMapSize());
  }

  @Test
  void reAddSameKey() {
    ExpirationThread expirationThread = new ExpirationThread(new ConcurrentExpireHashMap());

    Long expiration1 = System.currentTimeMillis() + 2000;
    expirationThread.addExpiration("key1",  expiration1);
    assertEquals(1,expirationThread.expirationMapSize());

    Long expiration2 = System.currentTimeMillis() + 2000;
    expirationThread.addExpiration("key1", expiration2);
    assertEquals(2,expirationThread.expirationMapSize());
  }

  @Test
  void remove() throws InterruptedException{
    ExpirationThread expirationThread = new ExpirationThread(new ConcurrentExpireHashMap());

    Long expiration1 = System.currentTimeMillis() + 2000;
    expirationThread.addExpiration("key1", expiration1);
    TimeUnit.MILLISECONDS.sleep(100);
    Long expiration2 = System.currentTimeMillis() + 2000;
    expirationThread.addExpiration("key2", expiration2);

    assertEquals(2, expirationThread.expirationMapSize());
    expirationThread.removeExpiration(expiration1, "key1");
    assertEquals(1, expirationThread.expirationMapSize());
    expirationThread.removeExpiration(expiration2, "key2");
    assertEquals(0, expirationThread.expirationMapSize());
  }

}