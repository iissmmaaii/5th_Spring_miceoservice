package com.cyphervault.api_gateway.common.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MutableHttpServletRequest extends HttpServletRequestWrapper {

    private final Map<String, HeaderValue> customHeaders = new LinkedHashMap<>();
    private final Set<String> removedHeaders = new HashSet<>();

    public MutableHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    public void putHeader(String name, String value) {
        String key = normalize(name);

        removedHeaders.remove(key);
        customHeaders.put(key, new HeaderValue(name, List.of(value)));
    }

    public void removeHeader(String name) {
        String key = normalize(name);

        customHeaders.remove(key);
        removedHeaders.add(key);
    }

    @Override
    public String getHeader(String name) {
        String key = normalize(name);

        if (customHeaders.containsKey(key)) {
            return customHeaders.get(key).values().get(0);
        }

        if (removedHeaders.contains(key)) {
            return null;
        }

        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        String key = normalize(name);

        if (customHeaders.containsKey(key)) {
            return Collections.enumeration(customHeaders.get(key).values());
        }

        if (removedHeaders.contains(key)) {
            return Collections.emptyEnumeration();
        }

        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> headerNames = new LinkedHashSet<>();

        Enumeration<String> originalHeaderNames = super.getHeaderNames();

        while (originalHeaderNames.hasMoreElements()) {
            String headerName = originalHeaderNames.nextElement();

            if (!removedHeaders.contains(normalize(headerName))) {
                headerNames.add(headerName);
            }
        }

        for (HeaderValue headerValue : customHeaders.values()) {
            headerNames.add(headerValue.originalName());
        }

        return Collections.enumeration(headerNames);
    }

    private String normalize(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    private record HeaderValue(
            String originalName,
            List<String> values
    ) {
    }
}