package io.smesh.properties;

import com.sun.javafx.runtime.SystemProperties;
import org.junit.Test;

import java.util.Collections;
import java.util.Properties;

import static org.junit.Assert.*;

public class SmeshPropertiesTest {


    @Test
    public void getStringTest() {
        final SmeshProperty withoutDefault = new SmeshProperty("withoutDefault");
        final SmeshProperty withDefault = new SmeshProperty("withDefault", "default-value");

        SmeshProperties props = new SmeshProperties(Collections.emptyMap());
        assertNull(props.getString(withoutDefault));
        assertEquals("default-value", props.getString(withDefault));

        Properties p = new Properties();
        p.put(withDefault.getName(), "explicit-value");
        assertEquals("explicit-value", new SmeshProperties(p).getRequiredString(withDefault));
    }

    @Test
    public void getStringFromSystemProperty() {
        final SmeshProperty withoutDefault = new SmeshProperty("test.withoutDefault");
        withoutDefault.setSystemProperty("some-value");

        assertEquals("some-value", new SmeshProperties(Collections.emptyMap()).getString(withoutDefault));
    }

    @Test(expected = NullPointerException.class)
    public void getRequiredStringThrowsException() {
        final SmeshProperty withoutDefault = new SmeshProperty("withoutDefault");
        new SmeshProperties(Collections.emptyMap()).getRequiredString(withoutDefault);
    }

    @Test
    public void getLongTest() {
        final SmeshProperty withoutDefault = new SmeshProperty("withoutDefault");
        final SmeshProperty withDefault = new SmeshProperty("withDefault", 1L);

        SmeshProperties props = new SmeshProperties(Collections.emptyMap());
        assertNull(props.getLong(withoutDefault));
        assertEquals(1L, props.getRequiredLong(withDefault));

        Properties p = new Properties();
        p.put(withDefault.getName(), "2");
        assertEquals(2L, new SmeshProperties(p).getRequiredLong(withDefault));
    }

    @Test(expected = NullPointerException.class)
    public void getRequiredLongThrowsException() {
        final SmeshProperty withoutDefault = new SmeshProperty("withoutDefault");
        new SmeshProperties(Collections.emptyMap()).getRequiredLong(withoutDefault);
    }

    @Test
    public void getBooleanTest() {
        final SmeshProperty withoutDefault = new SmeshProperty("withoutDefault");
        final SmeshProperty withDefault = new SmeshProperty("withDefault", true);

        SmeshProperties props = new SmeshProperties(Collections.emptyMap());
        assertNull(props.getBoolean(withoutDefault));
        assertTrue(props.getRequiredBoolean(withDefault));

        Properties p = new Properties();
        p.put(withDefault.getName(), "false");
        assertFalse(new SmeshProperties(p).getRequiredBoolean(withDefault));
    }

    @Test(expected = NullPointerException.class)
    public void getRequiredBooleanThrowsException() {
        final SmeshProperty withoutDefault = new SmeshProperty("withoutDefault");
        new SmeshProperties(Collections.emptyMap()).getRequiredBoolean(withoutDefault);
    }

    @Test
    public void getIntegerTest() {
        final SmeshProperty withoutDefault = new SmeshProperty("withoutDefault");
        final SmeshProperty withDefault = new SmeshProperty("withDefault", 1);

        SmeshProperties props = new SmeshProperties(Collections.emptyMap());
        assertNull(props.getInteger(withoutDefault));
        assertEquals(1, props.getRequiredInteger(withDefault));

        Properties p = new Properties();
        p.put(withDefault.getName(), "2");
        assertEquals(2, new SmeshProperties(p).getRequiredInteger(withDefault));
    }

    @Test(expected = NullPointerException.class)
    public void getRequiredIntegerThrowsException() {
        final SmeshProperty withoutDefault = new SmeshProperty("withoutDefault");
        new SmeshProperties(Collections.emptyMap()).getRequiredInteger(withoutDefault);
    }

    @Test
    public void getFloatTest() {
        final SmeshProperty withoutDefault = new SmeshProperty("withoutDefault");
        final SmeshProperty withDefault = new SmeshProperty("withDefault", 1.1f);

        SmeshProperties props = new SmeshProperties(Collections.emptyMap());
        assertNull(props.getFloat(withoutDefault));
        assertTrue(1.1f == props.getRequiredFloat(withDefault));

        Properties p = new Properties();
        p.put(withDefault.getName(), "2.2f");
        assertTrue(2.2f == new SmeshProperties(p).getRequiredFloat(withDefault));
    }

    @Test(expected = NullPointerException.class)
    public void getRequiredFloatThrowsException() {
        final SmeshProperty withoutDefault = new SmeshProperty("withoutDefault");
        new SmeshProperties(Collections.emptyMap()).getRequiredFloat(withoutDefault);
    }
}
