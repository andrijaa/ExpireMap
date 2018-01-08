import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Thread responsible for removing elements from @see ExpireMap based on the timeout provided for that element.
 * Internally it holds multimap of expirationTime->(Keys-to-expire) mapping, which allows to quickly find the set
 * of keys to expire. Since expirationTime is the key in the expiration map, first key is always the next one
 * to be expired.
 *
 * @author acolovic
 */
public class ExpirationThread extends Thread {

  private final ExpireMap expireMap;
  private final Map<Long, Set<Object>> expirationMultiMap = new HashMap<>();

  ExpirationThread(ExpireMap expireMap) {
    this.expireMap = expireMap;
  }

  @Override
  public void run() {
    while (true) {
      try {
        consume();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Consumer logic called by our ExpirationThread run loop.
   * <p>
   * 1. If no keys are to be expired, we just wait until Producer(ExpireMap) notifies us
   * 2. If there is a key in the map; we first check if it is time to expire that key. If not, we wait
   * the amount of time left to expire that key. This avoids unnecessary work by thread.
   * 3. Waiting operation is interrupted if a key is added/removed. And our code execution goes back to step #1.
   * <p>
   * Note that we process all keys that are supposed to expire in iteration in our dispatch loop.
   *
   * @throws InterruptedException
   */
  private synchronized void consume() throws InterruptedException {
    if (!expirationMultiMap.isEmpty()) {
      Map.Entry<Long, Set<Object>> nextToExpire = expirationMultiMap.entrySet().iterator().next();
      Long currentTimeMillis = System.currentTimeMillis();
      Long expirationTimeMillis = nextToExpire.getKey();
      if (expirationTimeMillis < currentTimeMillis) { // Is it time to expire Set<Key>?
        for (Object key : nextToExpire.getValue()) {
          expireMap.remove(key);
        }
      } else { // Key not expired yet, wait until it is time to expire it
        wait(expirationTimeMillis - currentTimeMillis);
      }
    } else { // queue empty
      wait();
    }
  }

  /**
   * Producer method that removes a key from our expiration queue
   *
   * @param key          key to be expired
   * @param expiration when to expire the key
   */
  public synchronized void addExpiration(Object key, long expiration) {
    Set<Object> keysToExpire = expirationMultiMap.computeIfAbsent(expiration, k -> new HashSet<>());
    keysToExpire.add(key);
    notify();
  }

  /**
   * Producer method. Remove a key from our expiration map
   *
   * @param key key/expiration pair to be removed
   */
  public synchronized void removeExpiration(Long expirationTimeMillis, Object key) {
    expireElement(expirationTimeMillis, key);
    notify();
  }

  private synchronized void expireElement(Long expirationTimeMillis, Object key) {
    Set<Object> keys = expirationMultiMap.get(expirationTimeMillis);
    if (keys != null) {
      keys.remove(key);
      if (keys.isEmpty()) {
        expirationMultiMap.remove(expirationTimeMillis);
      }
    }
  }

  public synchronized int expirationMapSize() {
    return expirationMultiMap.size();
  }
}
