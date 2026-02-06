## Code Review

You are reviewing the following code submitted as part of a task to implement an item cache in a highly concurrent application. The anticipated load includes: thousands of reads per second, hundreds of writes per second, tens of concurrent threads.
Your objective is to identify and explain the issues in the implementation that must be addressed before deploying the code to production. Please provide a clear explanation of each issue and its potential impact on production behaviour.

```kotlin
import java.util.concurrent.ConcurrentHashMap

class SimpleCache<K, V> {
    private val cache = ConcurrentHashMap<K, CacheEntry<V>>()
    private val ttlMs = 60000 // 1 minute
    
    data class CacheEntry<V>(val value: V, val timestamp: Long)
    
    fun put(key: K, value: V) {
        cache[key] = CacheEntry(value, System.currentTimeMillis())
    }
    
    fun get(key: K): V? {
        val entry = cache[key]
        if (entry != null) {
            if (System.currentTimeMillis() - entry.timestamp < ttlMs) {
                return entry.value
            }
        }
        return null
    }
    
    fun size(): Int {
        return cache.size
    }
}
```

## Review of SimpleCache

It uses ConcurrentHashMap and has a simple TTL mechanism. However, with the expected production load (high read volume, frequent writes, multiple concurrent threads), there are several issues that would need to be addressed before using this in a real system.

Below are the main problems I noticed and why they matter.

### Expired entries are never removed

The cache checks whether an entry is expired when get() is called, but expired entries are never actually removed from the map. They simply return null and stay in memory.

**Why this matters:** 
Over time, the cache will keep growing as new keys are added. Even though expired values aren’t returned anymore, they still occupy memory. Under steady traffic, this can lead to increased GC activity and eventually memory pressure or OOM issues.


### TTL checks are not atomic

The get() logic first reads the entry and then checks whether it has expired. These steps are not atomic, which means another thread could update or replace the entry in between.

**Why this matters:** 
In a concurrent environment, this can lead to inconsistent results, such as returning stale data or treating freshly written data as expired. These kinds of bugs are usually hard to reproduce and debug.

### No eviction or size limit

There is no limit on how many entries the cache can hold, and there is no eviction strategy in place.

**Why this matters:** 
Caches should always be bounded. Without a size limit, memory usage can grow without control, especially with hundreds of writes per second. This can slowly degrade performance and threaten JVM stability over time.

### Expired entries are rechecked on every read

Once an entry has expired, it remains in the map and is re-evaluated on every subsequent get().

**Why this matters:** 
This wastes CPU on repeated TTL checks for entries that are already known to be invalid. In a read-heavy system, this can add unnecessary overhead and reduce overall cache efficiency.

### Using System.currentTimeMillis() for TTL

TTL calculations are based on System.currentTimeMillis(), which relies on wall-clock time.

**Why this matters:** 
Wall-clock time can change due to NTP syncs or manual clock adjustments. If the clock moves backwards or forwards unexpectedly, entries may expire too early or too late, leading to confusing and inconsistent behavior in production.

### size() can be misleading

The size() method returns the total number of entries in the map, including expired ones.

**Why this matters:** 
If this value is used for monitoring or debugging, it can give a false picture of how many “active” cache entries actually exist. This can hide problems until memory usage becomes noticeable.

### No cleanup strategy

There is no background cleanup, scheduled task, or proactive removal of expired entries.

**Why this matters:** 
The cache relies entirely on reads to detect expiration but never performs real cleanup. Over time, this leads to a buildup of dead entries and gradual performance degradation.

### No observability

The implementation does not expose any metrics such as cache hits, misses, or expirations.

**Why this matters:** 
Without basic metrics, it’s hard to understand how the cache behaves under load or to tune TTL and capacity settings. This makes diagnosing performance issues much harder in production.

### My thoughts

Using ConcurrentHashMap provides thread-safe access to the map itself, but it does not make the overall cache logic safe or complete. As it stands, this implementation risks unbounded memory growth, inconsistent reads under concurrency, and gradual performance degradation. These issues would need to be addressed before deploying this cache in a high-throughput, multi-threaded environment.
