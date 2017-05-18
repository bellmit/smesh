package io.smesh.properties;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SmeshProperty {

    private final String name;
    private final String defaultValue;
    private final TimeUnit timeUnit;

    public SmeshProperty(String name) {
        this(name, (String) null);
    }

    public SmeshProperty(String name, boolean defaultValue) {
        this(name, defaultValue ? "true" : "false");
    }

    public SmeshProperty(String name, int defaultValue) {
        this(name, String.valueOf(defaultValue));
    }

    public SmeshProperty(String name, long defaultValue) {
        this(name, String.valueOf(defaultValue));
    }

    public SmeshProperty(String name, String defaultValue) {
        this(name, defaultValue, null);
    }

    protected SmeshProperty(String name, String defaultValue, TimeUnit timeUnit) {
        this.name = Objects.requireNonNull(name);
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
            throw new IllegalArgumentException(String.format("property %s has not TimeUnit defined", this));
        }
        return timeUnit;
    }

    public String getSystemProperty() {
        return System.getProperty(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
