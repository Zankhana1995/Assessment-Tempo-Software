//package cache;
//
//import com.github.benmanes.caffeine.cache.Cache;
//import com.github.benmanes.caffeine.cache.Caffeine;
//import java.util.concurrent.TimeUnit;
//
//public class ProfessionalCache {
//
//    // Build the cache with TTL and size limits
//    private final Cache<String, String> cache = Caffeine.newBuilder()
//            .expireAfterWrite(10, TimeUnit.MINUTES) // TTL
//            .maximumSize(10_000)                   // Memory protection
//            .recordStats()                          // Monitoring
//            .build();
//
//    public String getValue(String key) {
//        // This is the Caffeine version of computeIfAbsent
//        // It is fully atomic and thread-safe
//        return cache.get(key, k -> fetchDataFromDB(k));
//    }
//
//    private String fetchDataFromDB(String key) {
//        return "Data for " + key;
//    }
//}