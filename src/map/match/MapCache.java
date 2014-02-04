package map.match;

import java.util.*;

public class MapCache {
    private final Map<Key, Map<String, Object>> data = new TreeMap<Key, Map<String, Object>>();
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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{\n");
        for (Iterator<Map.Entry<Key, Map<String, Object>>> it = data.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Key, Map<String, Object>> entry = it.next();
            stringBuilder.append(entry.getKey().asMap());
            stringBuilder.append(":");
            stringBuilder.append(entry.getValue());
            if (it.hasNext()) {
                stringBuilder.append(",");
                stringBuilder.append("\n");
            }
        }
        stringBuilder.append("\n}");

        return stringBuilder.toString();
    }

    public static class Key implements Comparable<Key> {
        private final Map<String, Object> keyMap;

        public Key(Map<String, Object> keyMap) {
            this.keyMap = Collections.unmodifiableMap(new HashMap<String, Object>(keyMap));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (keyMap != null ? !keyMap.equals(key.keyMap) : key.keyMap != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return keyMap != null ? keyMap.hashCode() : 0;
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

        @Override
        public int compareTo(Key o) {
            if (!keyMap.keySet().containsAll(o.keyMap.keySet())) {
                throw new IllegalArgumentException();
            }

            String[] fieldList = new String[] {"id", "salesRef", "country", "underlying"};
            for (String field : fieldList) {
                Object value = keyMap.get(field);
                Object otherValue = o.keyMap.get(field);
                if (value != null && otherValue == null) {
                    return -1;
                } else if (value == null && otherValue != null) {
                    return 1;
                } else if (value != null && otherValue != null) {
                    int comparison = ((Comparable)value).compareTo(otherValue);
                    if (comparison != 0) {
                        return comparison;
                    }
                }
            }

            return 0;
        }
    }
}
