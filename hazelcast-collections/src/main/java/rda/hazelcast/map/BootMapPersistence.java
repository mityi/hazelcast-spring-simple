package rda.hazelcast.map;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapStore;
import rda.hazelcast.SomeData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class BootMapPersistence {
    public static final String NAME = BootMapPersistence.class.getCanonicalName();

    static HazelcastInstance hazelcastInstance;

    static IMap<String, SomeData> init() {
        MapConfig mapConfig = new MapConfig();
        mapConfig.setName(NAME);


        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setImplementation(new MapStoreFromDb());

        mapConfig.setMapStoreConfig(mapStoreConfig);

        Config config = new Config();
        config.addMapConfig(mapConfig);

        hazelcastInstance = Hazelcast.newHazelcastInstance(config);

        return hazelcastInstance.getMap(NAME);
    }

    public static void main(String[] args) {
        IMap<String, SomeData> map = init();

        final int numberOfEntriesToAdd = 1000;

        for (int i = 0; i < numberOfEntriesToAdd; i++) {
            String text = "t" + i;
            map.put(String.valueOf(i), new SomeData(String.valueOf(i)) {{
                setText(text);
            }});
        }

        System.out.printf("# Map store has %d elements\n", map.size());

        map.evictAll();
        System.out.printf("# After evictAll map size\t: %d\n", map.size());

        map.loadAll(Collections.singleton("55"), true);
        System.out.printf("# After loadAll(One element) map size\t: %d\n", map.size());

        map.loadAll(true);
        System.out.printf("# After loadAll map size\t: %d\n", map.size());

    }

    static class MapStoreFromDb implements MapStore<String, SomeData> {

        private ConcurrentMap<String, String> store = new ConcurrentHashMap();

        public void delete(String key) {
            store.remove(key);
        }

        public void store(String key, SomeData value) {
            store.put(key, value.getText());
        }

        public void storeAll(Map<String, SomeData> map) {
            for (Map.Entry<String, SomeData> entry : map.entrySet())
                store(entry.getKey(), entry.getValue());
        }

        public void deleteAll(Collection<String> keys) {
            for (String key : keys) delete(key);
        }

        public SomeData load(String key) {
            String text = store.get(key);
            if (text == null) return null;
            SomeData someData = new SomeData(key);
            someData.setText(text);
            return someData;
        }

        public Map<String, SomeData> loadAll(Collection<String> keys) {
            final Map map = new HashMap();
            for (String key : keys) {
                final SomeData value = load(key);
                map.put(key, value);
            }
            return map;
        }

        @Override
        public Set<String> loadAllKeys() {
            return store.keySet();
        }
    }

}
