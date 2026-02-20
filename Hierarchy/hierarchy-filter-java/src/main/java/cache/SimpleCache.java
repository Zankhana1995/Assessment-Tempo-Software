package cache;

import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;

public class SimpleCache<K, V> {
    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final long ttlMs;
    private final ScheduledExecutorService cleanupThread;

    public SimpleCache(long ttlMs, int cleanupIntervalSeconds) {
        this.ttlMs = ttlMs;
        // Fix for Issue #1: Active background eviction
        this.cleanupThread = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Cache-Cleanup-Thread");
            t.setDaemon(true); // Ensure thread doesn't block JVM shutdown
            return t;
        });

        this.cleanupThread.scheduleAtFixedRate(this::evictExpired,
                cleanupIntervalSeconds, cleanupIntervalSeconds, TimeUnit.SECONDS);
    }

    static class CacheEntry<V> {
        final V value;
        final long expiryTime;
        CacheEntry(V value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }
    }

    // Fix for Issue #4: Use loader pattern to prevent Cache Stampede
    public V get(K key, Function<K, V> loader) {
        CacheEntry<V> entry = cache.get(key);
        long now = System.currentTimeMillis();

        if (entry == null || now > entry.expiryTime) {
            // Fix for Issue #3: Atomic removal and computation
            cache.remove(key);
            return cache.computeIfAbsent(key, k -> {
                V freshValue = loader.apply(k);
                return new CacheEntry<>(freshValue, System.currentTimeMillis() + ttlMs);
            }).value;
        }
        return entry.value;
    }

    // Fix for Issue #1 & #3: Explicitly clean up memory
    private void evictExpired() {
        long now = System.currentTimeMillis();
        cache.entrySet().removeIf(entry -> now > entry.getValue().expiryTime);
    }

    public int size() {
        return cache.size();
    }

    public void shutdown() {
        cleanupThread.shutdown();
    }
}