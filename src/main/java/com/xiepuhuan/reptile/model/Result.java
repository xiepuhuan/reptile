package com.xiepuhuan.reptile.model;

import com.xiepuhuan.reptile.utils.ArgUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiepuhuan
 */
public class Result {

    private Map<String, Object> results;

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

    @Override
    public String toString() {
        return results.toString();
    }
}
