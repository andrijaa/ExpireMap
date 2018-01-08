# ExpireMap

## Interface
```java
interface ExpireMap<K,V> {
  /**
   * Insert a Key->Value pair into a map. 
   *   1. If there is no entry with the key in the map, add the key/value pair as a new entry. 
   *   2. If there is an existing entry with the key, the current entry will be replaced with the new key/value pair. 
   *   3. If the newly added entry is not removed after timeoutMs since it's added to the map, remove it.
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
   * Remove an key-value mapping from a map.
   *
   * @param key key used to lookup a specific pair
   */
  void remove(K key);
}
```

## Implementation
<b>ConcurrentExpireHashMap</b> is an implementation of <b>ExpireMap</b> interface. It supports access from multiple threads by using internally ConcurrentHashMap which ensures thread safety. On map creation, ConcurrentExpireHashMap starts a  background thread(<b>ExpirationThread</b>( that performs cleanup of expired keys. It comunicates with the thread(s) using the map via notify()/wait() methods. 

Expiration  waits until the next expiration is supposed to occur, before it kicks off removal of that key. If the new keyis added, expiration thread will check to see if the newly added key needs to be removed sooner than the current wait. If key removal occurs, we also cleanup any associated timeout info from the background thread and reset its waiting state. 

## Performance
Lookup via <b><i>get(K key)</i></b> in the map is <u>O(log n)</u> which equivalent of the performance of underlying internal ConcurrentHashMap lookup.

Insert via <b><i>put(K key, V value, long timeoutMs)</i></b> into our map is <u>O(log n)</u>.

Removal via <b><i>remove(K key)</i></b> from the map is <u>O(log n)</u>.
## Examples
	
### Add Key->Value pair with timeout
```java
    ExpireMap<String, String> map = new ConcurrentExpireHashMap<>();
    map.put("key1", "value1", 500);
    map.put("key2", "value2", 2000);
    map.put("key3", "value3", 5000);
```

### Remove Key->Value pair
```java
    ExpireMap<String, String> map = new ConcurrentExpireHashMap<>();
    map.put("key1", "value1", 5000);
    map.put("key2", "value2", 2000);
    map.remove("key1");
 ```
