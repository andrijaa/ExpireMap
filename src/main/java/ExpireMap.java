/**
 * Interface for a variation of Map storing key-value pairs that supports expiration of elements.
 *
 *
 * @author acolovic
 */
public interface ExpireMap<K,V> {

  /**
   * Insert a Key->Value pair into a map. If there is no entry with the key in the map,
   * add the key/value pair as a new entry. If there is an existing entry with the key,
   * the current entry will be replaced with the new key/value pair. If the newly added entry
   * is not removed after timeoutMs since it's added to the map, remove it.
   *
   * @param key key whose mapping is to be added to a map
   * @param value value that will be added under <tt>key</tt>
   * @param timeoutMs timeout in Milliseconds after which an entry will be expired and removed
   */
  void put(K key, V value, long timeoutMs);

  /**
   * Search for key-value pair mapping and return the value for specified key
   *
   * @param key key used to lookup a specific pair
   * @return Value from a map of V type
   */
  V get(K key);

  /**
   * Remove an key-value mapping from a map
   *
   * @param key key used to lookup a specific pair
   */
  void remove(K key);

  /**
   * Check if map is empty
   *
   * @return true/false whether a map is empty
   */
  boolean isEmpty();

  /**
   * Return the number of key-value pairs in the map
   *
   * @return integer size of map
   */
  int size();
}
