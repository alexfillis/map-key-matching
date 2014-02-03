package map.match;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PartialMatchKey {
    private final Map<String, Object> keyMap;

    public PartialMatchKey(Map<String, Object> keyMap) {
        this.keyMap = Collections.unmodifiableMap(new HashMap<String, Object>(keyMap));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartialMatchKey that = (PartialMatchKey) o;

//        if (!keyMap.equals(that.keyMap)) return false;

        if (keyMap.size() != that.keyMap.size()) return false;

        if (!keyMap.keySet().containsAll(that.keyMap.keySet())) return false;

        for (String keyElementName : keyMap.keySet()) {
            if (keyMap.get(keyElementName) != null && that.keyMap.get(keyElementName) != null && !keyMap.get(keyElementName).equals(that.keyMap.get(keyElementName))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return keyMap.hashCode();
    }
}
