package net.contextfw.web.application.url;

import java.util.Map;

public interface URLModel {
    public String getURL(String key, Map<String, String> params);
}
