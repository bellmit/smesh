package io.smesh.properties;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SmeshProperty {

    private final String name;
    private final String defaultValue;
    private final TimeUnit timeUnit;

    public SmeshProperty(String name) {
        this(name, null);
    }

    public SmeshProperty(String name, boolean defaultValue) {
        this(name, defaultValue ? "true" : "false");
    }

    public SmeshProperty(String name, int defaultValue) {
        this(name, String.valueOf(defaultValue));
    }

    public SmeshProperty(String name, int defaultValue, TimeUnit timeUnit) {
        this(name, String.valueOf(defaultValue), timeUnit);
    }

    public SmeshProperty(String name, float defaultValue) {
        this(name, String.valueOf(defaultValue));
    }

    public SmeshProperty(String name, float defaultValue, TimeUnit timeUnit) {
        this(name, String.valueOf(defaultValue), timeUnit);
    }

    public SmeshProperty(String name, long defaultValue) {
        this(name, String.valueOf(defaultValue));
    }

    public SmeshProperty(String name, long defaultValue, TimeUnit timeUnit) {
        this(name, String.valueOf(defaultValue), timeUnit);
    }

    public SmeshProperty(String name, String defaultValue) {
        this(name, defaultValue, null);
    }

    protected SmeshProperty(String name, String defaultValue, TimeUnit timeUnit) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.defaultValue = defaultValue;
        this.timeUnit = timeUnit;
    }

    public String getName() {
        return name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public TimeUnit getTimeUnit() {
        if (timeUnit == null) {
            throw new IllegalArgumentException(String.format("property %s has no TimeUnit defined", this));
        }
        return timeUnit;
    }

    public void setSystemProperty(String value) {
        System.setProperty(name, value);
    }

    public String getSystemProperty() {
        return System.getProperty(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
