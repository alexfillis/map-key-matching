package map.match.test;

import map.match.PartialMatchKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class PartialMatchMapTest {
    private final Map<PartialMatchKey, Map<String, Object>> data = new ConcurrentHashMap<PartialMatchKey, Map<String, Object>>();
    private final PartialMatchKey lookupKey;
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

    public PartialMatchMapTest(PartialMatchKey lookupKey, Map<String, Object> expectedValue) {
        initData();
        this.lookupKey = lookupKey;
        this.expectedValue = expectedValue;
    }

    @Test
    public void look_should_match_against_entry_in_data() {
        // execute
        Map<String, Object> mapValue = data.get(lookupKey);

        //verify
        verifyValue(expectedValue, mapValue);
    }

    private void initData() {
        PartialMatchKey key;
        Map<String, Object> value;

        key = key("12345", "First Bank AAA", "HK", ".HSI");
        value = value(new BigDecimal("0.002"), new BigDecimal("0.001"), new BigDecimal("0.004"), 3);
        data.put(key, value);

        key = key("12345", null, "HK", ".HSI");
        value = value(new BigDecimal("0.032"), new BigDecimal("0.016"), new BigDecimal("0.064"), 0);
        data.put(key, value);

        key = key("567890", "Best Bank BBB", "HK", null);
        value = value(new BigDecimal("0.004"), new BigDecimal("0.002"), new BigDecimal("0.008"), 2);
        data.put(key, value);

        key = key("567890", "Best Bank BBB", null, null);
        value = value(new BigDecimal("0.008"), new BigDecimal("0.004"), new BigDecimal("0.016"), 1);
        data.put(key, value);

        key = key("567890", null, null, null);
        value = value(new BigDecimal("0.016"), new BigDecimal("0.008"), new BigDecimal("0.032"), 0);
        data.put(key, value);
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

    private static PartialMatchKey key(String id, String salesRef, String country, String underlying) {
        Map<String, Object> key = new HashMap<String, Object>();
        key.put("id", id);
        key.put("salesRef", salesRef);
        key.put("country", country);
        key.put("underlying", underlying);
        return new PartialMatchKey(key);
    }

}
