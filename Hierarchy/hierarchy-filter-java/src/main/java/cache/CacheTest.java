package cache;

import java.util.concurrent.*;
import java.util.function.Function;

public class CacheTest {
    public static void main(String[] args) throws InterruptedException {
        // Initialize cache: 5 seconds TTL, cleanup every 2 seconds
        SimpleCache<String, String> myCache = new SimpleCache<>(5000, 2);

        // Simulation: 10 threads trying to get the same data at once
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // This is our "Slow Database Loader"
        Function<String, String> slowLoader = (key) -> {
            System.out.println("--- [DB] Fetching data for: " + key + " (Only happens once!) ---");
            try { Thread.sleep(1000); } catch (InterruptedException e) {} // Simulate DB delay
            return "FreshData_for_" + key;
        };

        System.out.println("Starting high-concurrency read...");

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                String val = myCache.get("User_1", slowLoader);
                System.out.println("Thread " + Thread.currentThread().getId() + " got: " + val);
            });
        }

        // Wait to see TTL in action
        System.out.println("\nWaiting 6 seconds for expiration...");
        Thread.sleep(6000);

        System.out.println("Accessing after expiration (should trigger 1 more DB fetch):");
        String expiredVal = myCache.get("User_1", slowLoader);
        System.out.println("Final Value: " + expiredVal);

        // Shutdown
        executor.shutdown();
        myCache.shutdown();
    }
}
