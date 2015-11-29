package rda.hazelcast.data.map;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class BootMapLocksAndEvictAll {

    public static final String NAME = BootMapLocksAndEvictAll.class.getCanonicalName();

    public static void main(String[] args) {
        final int numberOfKeysToLock = 4;
        final int numberOfEntriesToAdd = 1000;

        HazelcastInstance node1 = Hazelcast.newHazelcastInstance();
        HazelcastInstance node2 = Hazelcast.newHazelcastInstance();

        IMap<Integer, Integer> map = node1.getMap(NAME);
        for (int i = 0; i < numberOfEntriesToAdd; i++) {
            map.put(i, i);
        }

        Integer key = 5;
//        Pessimistic Locking
        CompletableFuture<Void> pessimistic = CompletableFuture.runAsync(() -> {
            System.out.println("Pessimistic Locking");
            map.lock(key);
            try {
                Integer value = map.get(key);
                TimeUnit.MILLISECONDS.sleep(3);
                value = ++value;
                map.put(key, value);
            } catch (InterruptedException ignore) {
            } finally {
                map.unlock(key);
            }
        }).thenRun(() -> {
            Integer value = map.get(key);
            System.out.printf("Pessimistic Locking value for key %d is %d\n", key, value);
        });

//        Optimistic Locking
        CompletableFuture<Void> optimistic = CompletableFuture.runAsync(() -> {
            try {
                for (; ; ) {
                    System.out.println("Optimistic Locking");
                    Integer oldValue = map.get(key);
                    Integer newValue = oldValue + 1;
                    TimeUnit.MILLISECONDS.sleep(3);
                    if (map.replace(key, oldValue, newValue)) break;
                }
            } catch (InterruptedException ignore) {
            }
        }).thenRun(() -> {
            Integer value = map.get(key);
            System.out.printf("Optimistic Locking value for key %d is %d\n", key, value);
        });

        CompletableFuture.allOf(pessimistic, optimistic).thenRun(() -> {

//        EvictAll
            for (int i = 0; i < numberOfKeysToLock; i++) {
                map.lock(i);
            }
            System.out.printf("# Actual map size\t: %d\n", map.size());

            // should keep locked keys and evict all others.
            node2.getMap(NAME).evictAll();

            System.out.printf("# After calling evictAll...\n");
            System.out.printf("# Expected map size\t: %d\n", numberOfKeysToLock);
            System.out.printf("# Actual map size\t: %d\n", map.size());
        });

    }
}