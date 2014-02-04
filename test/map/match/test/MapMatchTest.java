package map.match.test;

import map.match.MapCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.util.*;

import static map.match.test.MapMatchTest.buildCache;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class MapMatchTest {
    private final MapCache mapCache = buildCache();
    private final MapCache.Key lookupKey;
    private final Map<String, Object> expectedValue;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] {
                {
                        key("12345", "First Bank AAA", "HK", ".HSI"),
                        value(new BigDecimal("0.002"), new BigDecimal("0.001"), new BigDecimal("0.004"), 3)
                },
                {
                        key("567890", "Best Bank BBB", "HK", "0005.HK"),
                        value(new BigDecimal("0.004"), new BigDecimal("0.002"), new BigDecimal("0.008"), 2)
                },
                {
                        key("567890", "Best Bank BBB", "SG", "XXX.SG"),
                        value(new BigDecimal("0.008"), new BigDecimal("0.004"), new BigDecimal("0.016"), 1)
                },
                {
                        key("567890", "Best Bank V", "JP", ".N225"),
                        value(new BigDecimal("0.016"), new BigDecimal("0.008"), new BigDecimal("0.032"), 0)
                },
                {
                        key("12345", "First Bank BBB", "HK", ".HSI"),
                        value(new BigDecimal("0.016"), new BigDecimal("0.008"), new BigDecimal("0.032"), 0)
                }
        };
        return Arrays.asList(data);
    }

    public MapMatchTest(MapCache.Key lookupKey, Map<String, Object> expectedValue) {
        this.lookupKey = lookupKey;
        this.expectedValue = expectedValue;
    }

    @Test
    public void look_should_match_against_entry_in_data() {
        // execute
        Map<String, Object> mapValue = mapCache.match(lookupKey);

        //verify
        verifyValue(expectedValue, mapValue);
    }

    static MapCache buildCache() {
        MapCache mapCache = new MapCache(keysInPriorityOrder());
        MapCache.Key key;
        Map<String, Object> value;

        key = key("12345", "First Bank AAA", "HK", ".HSI");
        value = value(new BigDecimal("0.002"), new BigDecimal("0.001"), new BigDecimal("0.004"), 3);
        mapCache.put(key, value);

        key = key("12345", null, "HK", ".HSI");
        value = value(new BigDecimal("0.032"), new BigDecimal("0.016"), new BigDecimal("0.064"), 0);
        mapCache.put(key, value);

        key = key("567890", "Best Bank BBB", "HK", ".HSI");
        value = value(new BigDecimal("0.004"), new BigDecimal("0.002"), new BigDecimal("0.008"), 3);
        mapCache.put(key, value);

        key = key("567890", "Best Bank BBB", "HK", null);
        value = value(new BigDecimal("0.004"), new BigDecimal("0.002"), new BigDecimal("0.008"), 2);
        mapCache.put(key, value);

        key = key("567890", "Best Bank BBB", null, null);
        value = value(new BigDecimal("0.008"), new BigDecimal("0.004"), new BigDecimal("0.016"), 1);
        mapCache.put(key, value);

        key = key("567890", null, null, null);
        value = value(new BigDecimal("0.016"), new BigDecimal("0.008"), new BigDecimal("0.032"), 0);
        mapCache.put(key, value);

        return mapCache;
    }

    private void verifyValue(Map<String, Object> value, Map<String, Object> mapValue) {
        assertNotNull(mapValue);
        assertEquals(value, mapValue);
    }

    private static Map<String, Object> value(BigDecimal openCommission, BigDecimal rolloverCommission, BigDecimal closePermission, int settlementDateOffset) {
        Map<String, Object> value = new HashMap<String, Object>();
        value.put("openCommission", openCommission);
        value.put("rolloverCommission", rolloverCommission);
        value.put("closePermission", closePermission);
        value.put("settlementDateOffset", settlementDateOffset);
        return Collections.unmodifiableMap(value);
    }

    private static MapCache.Key key(String id, String salesRef, String country, String underlying) {
        Map<String, Object> key = new HashMap<String, Object>();
        key.put("id", id);
        key.put("salesRef", salesRef);
        key.put("country", country);
        key.put("underlying", underlying);
        return new MapCache.Key(key);
    }

    private static List<String> keysInPriorityOrder() {
        List<String> list = new ArrayList<String>();
        list.add("id");
        list.add("salesRef");
        list.add("country");
        list.add("underlying");
        return list;
    }
}

class Main {
    public static void main(String[] args) {
        System.out.println(buildCache());
    }
}