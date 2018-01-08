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

  private ConcurrentHashMap<K, ValueExpirationPair> internalMap;
  private ExpirationThread expirationThread;

  public ConcurrentExpireHashMap() {
    internalMap = new ConcurrentHashMap<>();
    expirationThread = new ExpirationThread(this);
    expirationThread.start();
  }

  @Override
  public void put(K key, V value, long timeoutMs) {
    Long expiration = System.currentTimeMillis() + timeoutMs;
    if (internalMap.put(key, new ValueExpirationPair(value, expiration)) != null) {
      expirationThread.removeExpiration(expiration, key); // remove previous expiration if Key-Value updated
    }
    expirationThread.addExpiration(key, expiration);
  }

  @Override
  public Object get(Object key) {
    return internalMap.get(key);
  }

  @Override
  public void remove(Object key) {
    ValueExpirationPair valueExpirationPair = internalMap.remove(key);
    if (valueExpirationPair != null) {
      expirationThread.removeExpiration(valueExpirationPair.getExpiration(), key);
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
