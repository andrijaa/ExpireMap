# ExpireMap

## Implementation
ConcurrentExpireHashMap is an implementation of ExpireMap interface. It supports access from multiple threads by using internally ConcurrentHashMap which ensures thread safety.Also, internally ConcurrentExpireHashMap has a background thread that performs cleanup of expired elements. 

Expiration(cleanup) thread starts on map creation, and it waits until the next expiration is supposed to occur, before it kicks off removal of that element.

If the new element is added, expiration thread will check to see if the newly added element needs to be removed sooner than the current wait.

For the removed element, background thread will ensure it stops waiting to expire the removed element; and then it finds the next element(s) to expire and resets the waiting step.


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
