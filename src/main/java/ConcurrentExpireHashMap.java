import java.util.concurrent.ConcurrentHashMap;

/**
 *  Thread safe implementation of Map storing key->value pairs with expiration support. Each key->value pair is
 *  added with timeout value which defines key-value expiration. Internally all key-value pairs are stores in a
 *  @see ConcurrentHashMap which concurrent access from multiple threads.
 *
 *  Internal map keeps a relationship of Key->ValueExpirationPair. @see ValueExpirationPair helps us keep track of
 *  expiration time related to that Key->Value pair; so we can perform a O(log n) removal of the timeout entry
 *
 *  @see ExpirationThread internally is our cleanup thread, which expires keys based on the defined per-key timeout.
 *
 *  @author acolovic
 */
public class ConcurrentExpireHashMap<K, V> implements ExpireMap<K, V> {

  private ConcurrentHashMap<K, ValueExpirationPair<V>> internalMap;
  private ExpirationThread expirationThread;

  public ConcurrentExpireHashMap() {
    internalMap = new ConcurrentHashMap<>();
    expirationThread = new ExpirationThread(this);
    expirationThread.start();
  }

  @Override
  public void put(K key, V value, long timeoutMs) {
    Long expiration = System.currentTimeMillis() + timeoutMs;
    ValueExpirationPair previous = internalMap.put(key, new ValueExpirationPair(value, expiration));
    if (previous!= null) {
      expirationThread.removeExpiration(previous.getExpiration(), key); // remove previous expiration if Key-Value updated
    }
    expirationThread.addExpiration(key, expiration);
  }

  @Override
  public Object get(Object key) {
    ValueExpirationPair valueExpirationPair = internalMap.get(key);
    if (valueExpirationPair != null){
      return valueExpirationPair.getValue();
    }
    return null;
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
