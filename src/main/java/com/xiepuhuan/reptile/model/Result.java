package com.xiepuhuan.reptile.model;

import com.xiepuhuan.reptile.utils.ArgUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiepuhuan
 */
@SuppressWarnings("all")
public class Result {

    public static final String MONGODB_DATABASE_COLLECTION_NAME = "mongodbCollectionName";

    private Map<String, Object> results;

    private Map<String, Object> extendedFields;

    /**
     * 当ignore为true时，该结果会被忽略，即不会被消费
     */
    private boolean ignore = false;

    public Result(Map<String, Object> results) {
        ArgUtils.notNull(results, "results");
        this.results = results;
    }

    public Result() {
        this(new HashMap<>());
    }

    public Map<String, Object> getResults() {
        return results;
    }

    public <T> T getResult(String name) {
        Object value = results.get(name);
        if (value == null) {
            return null;
        }

        return (T) value;
    }

    public <T> Result setResult(String name, T value) {
        this.results.put(name, value);
        return this;
    }

    public Result setResults(Map<String, Object> results) {
        this.results = results;
        return this;
    }

    public <T> Result setExtendedField(String name, T value) {
        if (extendedFields == null) {
            extendedFields = new HashMap<>();
        }
        this.results.put(name, value);
        return this;
    }

    public <T> T getExtendedField(String name) {
        if (extendedFields == null) {
            return null;
        }

        Object value = results.get(name);
        if (value == null) {
            return null;
        }
        return (T) value;
    }

    public Result setIgnore(boolean ignore) {
        this.ignore = ignore;
        return this;
    }

    public boolean isIgnore() {
        return ignore;
    }

    @Override
    public String toString() {
        return results.toString();
    }
}
