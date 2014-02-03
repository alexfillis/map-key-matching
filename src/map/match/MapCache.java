package map.match;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MapCache {
    private final Map<Key, Map<String, Object>> data = new ConcurrentHashMap<Key, Map<String, Object>>();
    private final List<String> prioritisedKeys;

    public MapCache(List<String> prioritisedKeys) {
        this.prioritisedKeys = Collections.unmodifiableList(new ArrayList<String>(prioritisedKeys));
    }

    public void put(Key key, Map<String, Object> value) {
        data.put(key, value);
    }

    public Map<String, Object> match(Key key) {
        if (!validKey(key)) {
            throw new IllegalArgumentException();
        }
        return get(key, prioritisedKeys);
    }

    private boolean validKey(Key key) {
        return key.hasAll(prioritisedKeys);
    }

    private Map<String, Object> get(Key key, List<String> priorityKeyList) {
        Map<String, Object> value = data.get(key);
        if (value == null && priorityKeyList.size() > 1) {
            value = get(subKey(key, priorityKeyList.get(priorityKeyList.size() - 1)), priorityKeyList.subList(0, priorityKeyList.size() - 1));
        }
        return value;
    }

    private Key subKey(Key key, String s) {
        if (!key.has(s)) {
            throw new IllegalArgumentException();
        }
        Map<String, Object> subKey = new HashMap<String, Object>(key.asMap());
        subKey.put(s, null);
        return new Key(subKey);
    }

    public static class Key {
        private final Map<String, Object> keyMap;

        public Key(Map<String, Object> keyMap) {
            this.keyMap = Collections.unmodifiableMap(new HashMap<String, Object>(keyMap));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key1 = (Key) o;

            Map<String, Object> thisKeyMap = this.asMap();
            Map<String, Object> key1KeyMap = key1.asMap();

            if (thisKeyMap.size() !=  key1KeyMap.size()) {
                return false;
            }

            for (String key : thisKeyMap.keySet()) {
                if (key1KeyMap.containsKey(key)) {
                    Object thisKeyValue = thisKeyMap.get(key);
                    Object key1Value = key1KeyMap.get(key);
                    if (thisKeyValue != null && !thisKeyValue.equals(key1Value)) {
                        return false;
                    }
                }
            }

            return true;
        }

        @Override
        public int hashCode() {
            return keyMap.hashCode();
        }

        public boolean hasAll(List<String> keyKeys) {
            for (String keyKey : keyKeys) {
                if (!keyMap.containsKey(keyKey)) {
                    return false;
                }
            }
            return true;
        }

        public boolean has(String keyKey) {
            return keyMap.containsKey(keyKey);
        }

        public Map<String, Object> asMap() {
            return keyMap;
        }
    }
}
