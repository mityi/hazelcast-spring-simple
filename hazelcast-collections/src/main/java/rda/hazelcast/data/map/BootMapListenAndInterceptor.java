package rda.hazelcast.data.map;


import com.hazelcast.core.*;
import com.hazelcast.map.MapInterceptor;
import com.hazelcast.map.listener.*;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class BootMapListenAndInterceptor {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            HazelcastInstance hz1 = Hazelcast.newHazelcastInstance();
            IMap<String, String> map = hz1.getMap("somemap");
            String id = map.addEntryListener(new MapEntryListener(), true);

            System.out.println("Entry/Map Listener registered " + id);

        }).thenRun(() -> {
            HazelcastInstance hz2 = Hazelcast.newHazelcastInstance();
            IMap<String, String> map = hz2.getMap("somemap");
            String id = map.addInterceptor(new SimpleInterceptor());

            System.out.println("Interceptor registered " + id);

            map.put("1", "New York");
            map.put("2", "Istanbul");
            map.put("3", "Tokyo");
            map.delete("1");
            try {
                map.evict("2");
            } catch (RuntimeException ignore) {
            }
            System.out.println("After interceptor 3 = " + map.get("3"));

            map.evictAll();

            map.put("4", "London");
            map.clear();

            map.put("5", "Paris");
            map.removeInterceptor(id);
            map.put("6", "Cairo");
            System.out.println("After remove interceptor 5 = " + map.get("5"));
            System.out.println("After remove interceptor 6 = " + map.get("6"));

        });

        while (!future.isDone()) ;
    }

    static class SimpleInterceptor implements MapInterceptor, Serializable {

        @Override
        public Object interceptGet(Object value) {
            if (value == null)
                return null;
            return ":" + value + ":";
        }

        @Override
        public Object interceptPut(Object oldValue, Object newValue) {
            return newValue.toString().toUpperCase();
        }

        @Override
        public Object interceptRemove(Object removedValue) {
            if (removedValue.equals("ISTANBUL"))
                throw new RuntimeException("you can not remove this");
            return removedValue;
        }

        @Override
        public void afterGet(Object value) {
        }

        @Override
        public void afterPut(Object value) {
        }

        @Override
        public void afterRemove(Object value) {
            // do something
        }
    }

    static class MapEntryListener implements
            EntryAddedListener<String, String>,
            EntryRemovedListener<String, String>,
            EntryUpdatedListener<String, String>,
            EntryEvictedListener<String, String>,
//            EntryMergedListener<String, String>,
            MapEvictedListener,
            MapClearedListener {

//        A map listener runs on the event threads that are also used by the other listeners
//        private Executor executor = Executors.newFixedThreadPool(5);
//        public void entryAdded(EntryEvent event) {
//            executor.execute(new DoSomethingWithEvent(event));
//        }

        @Override
        public void entryAdded(EntryEvent<String, String> event) {
            System.out.println("Entry Added:" + event);
        }

        @Override
        public void entryRemoved(EntryEvent<String, String> event) {
            System.out.println("Entry Removed:" + event);
        }

        @Override
        public void entryUpdated(EntryEvent<String, String> event) {
            System.out.println("Entry Updated:" + event);
        }

        @Override
        public void entryEvicted(EntryEvent<String, String> event) {
            System.out.println("Entry Evicted:" + event);
        }

        @Override
        public void mapEvicted(MapEvent event) {
            System.out.println("Map Evicted:" + event);
        }

        @Override
        public void mapCleared(MapEvent event) {
            System.out.println("Map Cleared:" + event);
        }

    }

}

