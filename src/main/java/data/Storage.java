package data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Storage {
  private static ConcurrentHashMap<String, String> data = new ConcurrentHashMap<>();
  private static ConcurrentHashMap<String, Long> expiry = new ConcurrentHashMap<>();
  private static final ScheduledExecutorService expirationScheduler =
      Executors.newScheduledThreadPool(1);

  public static void set(String key, String val) {
    data.put(key, val);
  }

  public static void set(String key, String val, Long expiryAt) {
    if (expiryAt != null) {
      expiry.put(key, expiryAt);
    }

    data.put(key, val);
  }

  public static String get(String key) {
    Long expiryAt = expiry.get(key);

    if (expiryAt != null && expiryAt < System.currentTimeMillis()) {
      delete(key);
    }

    return data.get(key);
  }

  public static void delete(String key) {
    expiry.remove(key);
    data.remove(key);
  }

  public static List<String> getKeys() {
    return new ArrayList<String>(data.keySet());
  }

  public static void runCleanUp(long ttl, TimeUnit timeUnit) {
    expirationScheduler.scheduleAtFixedRate(Storage::cleanUp, ttl, ttl, timeUnit);
  }

  public static void cleanUp() {
    long now = System.currentTimeMillis();

    for (Map.Entry<String, Long> entry : expiry.entrySet()) {
      Long expirationTime = entry.getValue();

      if (expirationTime != null && now > expirationTime) {
        delete(entry.getKey());
      }
    }
  }
}
