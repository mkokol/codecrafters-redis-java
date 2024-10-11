package data;

import java.util.HashMap;

public class Storage {
    private static HashMap<String, String> data = new HashMap<>();

    public static void set(String key, String val) {
        data.put(key, val);
    }

    public static String get(String key) {
        return data.get(key);
    }
}

