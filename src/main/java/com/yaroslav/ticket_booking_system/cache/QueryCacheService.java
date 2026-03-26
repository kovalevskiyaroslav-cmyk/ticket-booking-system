package com.yaroslav.ticket_booking_system.cache;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class QueryCacheService {

    private final Map<QueryKey, Object> cache = new ConcurrentHashMap<>();

    public <T> Page<T> getPage(QueryKey key, Class<T> elementType) {
        final Object value = cache.get(key);

        if (value instanceof Page<?>) {
            return ((Page<?>) value).map(elementType::cast);
        }

        return null;
    }

    public void put(QueryKey key, Object data) {
        cache.put(key, data);
    }

    public boolean containsKey(QueryKey key) {
        return cache.containsKey(key);
    }

    public void evictByPattern(String pattern) {

        cache.keySet().removeIf(key ->
                key.toString().contains(pattern)
        );
    }
}