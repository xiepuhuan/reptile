package com.xiepuhuan.reptile.utils;

import com.xiepuhuan.reptile.model.Cookie;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiepuhuan
 */
public class CookieUtils {

    public static List<Cookie> string2Cookie(String cookieStr, String domain) {
        ArgUtils.notEmpty(cookieStr, "cookieStr");

        String[] cookieStrSub = cookieStr.split("; ");
        List<Cookie> cookies = new ArrayList<>(cookieStrSub.length);
        for (String c : cookieStrSub) {
            int i = c.indexOf("=");
            if (i >= 0) {
                cookies.add(new Cookie(c.substring(0, i), c.substring(i + 1), domain));
            }
        }

        return cookies;
    }
}
