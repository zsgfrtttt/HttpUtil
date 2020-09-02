package com.csz.http.param;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author caishuzhan
 */
public class HttpHeader implements NameValueMap {

    public final static String ACCEPT = "Accept";
    public final static String ACCEPT_ENCODING = "Accept-Encoding";
    public final static String PRAGMA = "Pragma";
    public final static String CACHE_CONTROL = "Cache-control";
    public final static String USER_AGENT = "User-agent";
    public final static String PROXY_CONNECTION = "Proxy-Connection";
    public final static String CONNECTION = "Connection";

    public final static String CONTENT_ENCODING = "Content-Encoding";
    public final static String CONTENT_LENGTH = "Content-Length";
    public final static String CONTENT_TYPE = "Content-Type";
    public final static String LAST_MODIFIED = "Last-Modified";


    private Map<String, String> mMap = new HashMap<>();

    public String getAccept() {
        return get(ACCEPT);
    }

    public void setAccept(String val) {
        put(ACCEPT, val);
    }

    public String getPragma() {
        return get(PRAGMA);
    }

    public void setPragma(String val) {
        put(PRAGMA, val);
    }

    public String getUserAgent() {
        return get(USER_AGENT);
    }

    public void setUserAgent(String val) {
        put(USER_AGENT, val);
    }

    public String getProxyConnection() {
        return get(PROXY_CONNECTION);
    }

    public void setProxyConnection(String val) {
        put(PROXY_CONNECTION, val);
    }

    public String getAcceptEncoding() {
        return get(ACCEPT_ENCODING);
    }

    public void setAcceptEncoding(String val) {
        put(ACCEPT_ENCODING, val);
    }

    public String getCacheControl() {
        return get(CACHE_CONTROL);
    }

    public void setCacheControl(String val) {
        put(CACHE_CONTROL, val);
    }

    public String getContentType() {
        return get(CONTENT_TYPE);
    }

    public void setContentType(String val) {
        put(CONTENT_TYPE, val);
    }

    public String getContentEncoding() {
        return get(CONTENT_ENCODING);
    }

    public void setContentEncoding(String val) {
        put(CONTENT_ENCODING, val);
    }

    public String getConnection() {
        return get(CONNECTION);
    }

    public void setConnection(String val) {
        put(CONNECTION, val);
    }

    public String getContentLength() {
        return get(CONTENT_LENGTH);
    }

    public void setContentLength(String val) {
        put(CONTENT_LENGTH, val);
    }

    public String getLastModified() {
        return get(LAST_MODIFIED);
    }

    public void setLastModified(String val) {
        put(LAST_MODIFIED, val);
    }


    @Override
    public int size() {
        return mMap.size();
    }

    @Override
    public boolean isEmpty() {
        return mMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return mMap.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return mMap.containsValue(o);
    }

    @Override
    public String get(Object o) {
        return mMap.get(o);
    }

    @Override
    public String put(String s, String s2) {
        return mMap.put(s, s2);
    }

    @Override
    public String remove(Object o) {
        return mMap.remove(o);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> map) {
        mMap.putAll(map);
    }

    @Override
    public void clear() {
        mMap.clear();
    }

    @Override
    public Set<String> keySet() {
        return mMap.keySet();
    }

    @Override
    public Collection<String> values() {
        return mMap.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return mMap.entrySet();
    }
}
