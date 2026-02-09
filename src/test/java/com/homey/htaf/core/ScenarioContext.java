package com.homey.htaf.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScenarioContext {
    private static final ScenarioContext INSTANCE = new ScenarioContext();
    private static final ThreadLocal<Map<String, Object>> CONTEXT =
            ThreadLocal.withInitial(ConcurrentHashMap::new);

    public static ScenarioContext getInstance() {
        return INSTANCE;
    }

    public void set(String key, Object value) {
        CONTEXT.get().put(key, value);
    }

    public Object get(String key) {
        return CONTEXT.get().get(key);
    }

    public <T> T get(String key, Class<T> type) {
        Object value = CONTEXT.get().get(key);
        if (value == null) {
            return null;
        }
        return type.cast(value);
    }

    public void remove(String key) {
        CONTEXT.get().remove(key);
    }

    public void clear() {
        CONTEXT.get().clear();
    }
}
