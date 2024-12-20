package data;

import java.util.concurrent.ConcurrentHashMap;

public class Storage {
  private static ConcurrentHashMap<String, String> data = new ConcurrentHashMap<>();

  public static void set(String key, String val) {
    data.put(key, val);
  }

  public static String get(String key) {
    return data.get(key);
  }

  public static void delete(String key) {
    data.remove(key);
  }
}
