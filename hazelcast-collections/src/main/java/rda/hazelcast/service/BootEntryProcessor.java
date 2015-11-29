package rda.hazelcast.service;


import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.AbstractEntryProcessor;
import com.hazelcast.map.listener.EntryUpdatedListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class BootEntryProcessor {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Set<String> set = new HashSet<>(Arrays.asList("1", "2", "3"));
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            HazelcastInstance hz1 = Hazelcast.newHazelcastInstance();
            IMap<String, Integer> map = hz1.getMap("somemap");
            EntryUpdatedListener<String, String> stringStringEntryUpdatedListener =
                    event -> System.out.println("Entry Updated:" + event);
            map.addLocalEntryListener(stringStringEntryUpdatedListener);
            set.forEach(k -> map.put(k, Integer.valueOf(k)));
            set.forEach(k -> map.put(k, Integer.valueOf(k) + 10));

        }).thenRun(() -> {
            HazelcastInstance hz2 = Hazelcast.newHazelcastInstance();
            IMap<String, Integer> map = hz2.getMap("somemap");

            Set<String> localKeySet = map.localKeySet();
            set.removeAll(localKeySet);
            System.out.println("out keys:" + set);
            map.executeOnKeys(set, new AbstractEntryProcessor<String, Integer>() {
                @Override
                public Object process(Map.Entry<String, Integer> entry) {
                    return entry.setValue(2);
                }
            });
//            Object executeOnKey( K key, EntryProcessor entryProcessor );
//            void submitToKey( K key, EntryProcessor entryProcessor, ExecutionCallback callback );
//            Map<K, Object> executeOnEntries( EntryProcessor entryProcessor );
//            Map<K, Object> executeOnEntries( EntryProcessor entryProcessor, Predicate predicate );
        });

        future.get();
    }
}
