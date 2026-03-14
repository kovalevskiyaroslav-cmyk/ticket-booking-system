package com.yaroslav.ticket_booking_system.cache;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class QueryCacheService {

    private final Map<QueryKey, Object> cache = new ConcurrentHashMap<>();

    public <T> T get(QueryKey key, Class<T> type) {
        return type.cast(cache.get(key));
    }

    public <T> List<T> getList(QueryKey key, Class<T> elementType) {
        Object value = cache.get(key);
        if (value instanceof List<?>) {
            return ((List<?>) value).stream()
                    .map(elementType::cast)
                    .collect(Collectors.toList());
        }
        return null;
    }

    public <T> Page<T> getPage(QueryKey key, Class<T> elementType) {
        Object value = cache.get(key);
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

    public void evict(QueryKey key) {
        cache.remove(key);
    }

    public void evictByPattern(String pattern) {
        cache.keySet().removeIf(key ->
                key.toString().contains(pattern)
        );
    }
}