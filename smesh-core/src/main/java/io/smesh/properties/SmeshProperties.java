package io.smesh.properties;

import java.util.*;

public class SmeshProperties {

    private final Map<String,String> properties = new HashMap<>();

    public SmeshProperties(Properties nullableProperties) {
        if (nullableProperties != null) {
            nullableProperties.forEach((key, value) -> properties.put((String)key, (String) value));
        }
    }

    public SmeshProperties(Map<String,String> nullableProperties) {
        if (nullableProperties != null) {
            properties.putAll(nullableProperties);
        }
    }

    public Set<String> keySet() {
        return Collections.unmodifiableSet(properties.keySet());
    }

    public String getString(SmeshProperty property) {
        String value = properties.get(property.getName());
        if (value != null) {
            return value;
        }

        value = property.getSystemProperty();
        if (value != null) {
            return value;
        }

        return property.getDefaultValue();
    }

    public String getRequiredString(SmeshProperty property) {
        String value = getString(property);
        if (value == null) {
            throw new NullPointerException(String.format("property %s is required", property.getName()));
        }
        return value;
    }

    public Boolean getBoolean(SmeshProperty property) {
        String value = getString(property);
        return value == null ? null : Boolean.valueOf(value);
    }

    public Boolean getRequiredBoolean(SmeshProperty property) {
        return Boolean.valueOf(getRequiredString(property));
    }

    public long getLong(SmeshProperty property) {
        return Long.valueOf(getString(property));
    }

    public int getInt(SmeshProperty property) {
        return Integer.valueOf(getString(property));
    }

    public float getFloat(SmeshProperty property) {
        return Float.valueOf(getString(property));
    }
}
