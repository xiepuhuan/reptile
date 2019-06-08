package com.xiepuhuan.reptile.model;

import com.xiepuhuan.reptile.utils.ArgUtils;

/**
 * @author xiepuhuan
 */
public class Header {

    private final String name;

    private final String value;

    public Header(String name, String value) {
        ArgUtils.notNull(name, "name");
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Header{");
        sb.append("name='").append(name).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
