import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeUnit;

class ConcurrentExpireHashMapTest {

  @Test
  void putAndGet() {
    ExpireMap<String, String> testMap = new ConcurrentExpireHashMap<>();
    assertEquals(0, testMap.size());
    assertEquals(true, testMap.isEmpty());
    testMap.put("key1", "value1",10000);
    assertEquals("value1", testMap.get("key1"));
  }

  @Test
  void singlePutThatExpired() throws InterruptedException {
    ExpireMap<String, String> testMap = new ConcurrentExpireHashMap<>();
    testMap.put("key1", "value1",500);
    TimeUnit.MILLISECONDS.sleep(1000);
    assertEquals(null, testMap.get("key1"));
    assertEquals(0, testMap.size());
  }

  @Test
  void putsThatExpired() throws InterruptedException {
    ExpireMap<String, String> testMap = new ConcurrentExpireHashMap<>();
    testMap.put("key1", "value1",300);
    testMap.put("key2", "value2",400);
    testMap.put("key3", "value3",500);
    TimeUnit.MILLISECONDS.sleep(1000);
    assertEquals(null, testMap.get("key1"));
    assertEquals(null, testMap.get("key2"));
    assertEquals(null, testMap.get("key3"));
    assertEquals(0, testMap.size());
  }

  @Test
  void remove() throws InterruptedException {
    ExpireMap<String, String> testMap = new ConcurrentExpireHashMap<>();
    testMap.put("key1", "value1",10000);
    testMap.remove("key1");
    assertEquals(null, testMap.get("key1"));
  }

  @Test
  void readingWithSameKey() throws InterruptedException {
    ExpireMap<String, String> testMap = new ConcurrentExpireHashMap<>();
    testMap.put("key1", "value1",1000);
    assertEquals("value1", testMap.get("key1"));
    assertEquals(1, testMap.size());
    testMap.put("key1", "value1",10000);
    TimeUnit.MILLISECONDS.sleep(1100);
    assertEquals("value1", testMap.get("key1"));
    assertEquals(1, testMap.size());
  }
}