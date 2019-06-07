package com.xiepuhuan.reptile.model;

import com.xiepuhuan.reptile.utils.ArgUtils;
import java.text.DateFormat;
import java.util.Date;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * @author xiepuhuan
 */
public class Cookie {

    public static final String DEFAULT_PATH = "/";

    /** Cookie name */
    private final String name;

    /** Cookie value */
    private String value;

    /** Domain attribute. */
    private String  domain;

    /** Expiration {@link Date}. */
    private Date expiryDate;

    /** Path attribute. */
    private String path;

    /** My secure flag. */
    private boolean isSecure;

    public Cookie(String name, String value, String domain) {
        ArgUtils.notEmpty(name, "cookie name");
        ArgUtils.notEmpty(value, "cookie value");
        ArgUtils.notEmpty(domain, "cookie domain");
        this.name = name;
        this.value = value;
        this.domain = domain;
        this.path = DEFAULT_PATH;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public Cookie setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }

    public String getPath() {
        return path;
    }

    public Cookie setPath(String path) {
        this.path = path;
        return this;
    }

    public boolean isSecure() {
        return isSecure;
    }

    public Cookie setSecure(boolean secure) {
        isSecure = secure;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getDomain() {
        return domain;
    }
}
