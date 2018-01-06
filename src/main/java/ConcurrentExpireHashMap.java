import java.util.concurrent.ConcurrentHashMap;

/**
 *  Thread safe implementation of Map storing key->value pairs with expiration support. Each key->value pair is
 *  added with timeout value which defines key-value expiration. Internally all key-value pairs are stores in a
 *  @see ConcurrentHashMap which ensures thread safety.
 *
 *  Also, our implementation contains a @see ExpirationThread acting as a cleanup thread, expiring elements.
 *
 *  @author acolovic
 */
public class ConcurrentExpireHashMap<K, V> implements ExpireMap<K, V> {

  private ConcurrentHashMap<K, V> internalMap;
  private ExpirationThread expirationThread;

  public ConcurrentExpireHashMap() {
    internalMap = new ConcurrentHashMap<>();
    expirationThread = new ExpirationThread(this);
    expirationThread.start();
  }

  @Override
  public void put(K key, V value, long timeoutMs) {
    if (internalMap.put(key, value) != null) {
      expirationThread.removeExpiration(key); // remove previous expiration if Key-Value updated
    }
    expirationThread.addExpiration(key, timeoutMs);
  }

  @Override
  public Object get(Object key) {
    return internalMap.get(key);
  }

  @Override
  public void remove(Object key) {
    if (internalMap.remove(key) != null) {
      expirationThread.removeExpiration(key);
    }
  }

  @Override
  public boolean isEmpty() {
    return internalMap.isEmpty();
  }

  @Override
  public int size() {
    return internalMap.size();
  }
}
