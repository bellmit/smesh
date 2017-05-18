package io.smesh.properties;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SmeshPropertyTest {

    @Test(expected = IllegalArgumentException.class)
    public void getTimeUnitThrowsException() {
        new SmeshProperty("some.key").getTimeUnit();
    }

    @Test
    public void returnsDefaultValue() {
        SmeshProperty prop = new SmeshProperty("some.key", "some-default");
        assertNotNull(prop.getDefaultValue());
    }

    @Test
    public void returnsTimeUnit() {
        SmeshProperty prop = new SmeshProperty("some.key", null, TimeUnit.SECONDS);
        assertEquals(TimeUnit.SECONDS, prop.getTimeUnit());
    }

    @Test
    public void getSystemPropertyReturnsNull() {
        assertNull(new SmeshProperty("some.key1").getSystemProperty());
    }

    @Test
    public void getSystemPropertyReturnsValue() {
        SmeshProperty property = new SmeshProperty("some.sysprop.key");
        property.setSystemProperty("value");
        assertEquals("value", property.getSystemProperty());
    }
}