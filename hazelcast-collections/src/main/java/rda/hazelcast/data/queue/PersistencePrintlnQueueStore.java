package rda.hazelcast.data.queue;

import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.QueueStoreConfig;
import com.hazelcast.core.QueueStore;
import rda.hazelcast.SomeData;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PersistencePrintlnQueueStore implements QueueStore<SomeData> {

    public static QueueConfig queueConfigWithStory(String name) {
        QueueConfig queueConfig = new QueueConfig();
        queueConfig.setName(name);
        QueueStoreConfig queueStoreConfig = new QueueStoreConfig();
        queueStoreConfig.setStoreImplementation(new PersistencePrintlnQueueStore());

        Properties properties = new Properties();
//  If query is local. This would be a performance optimization
        properties.put("binary", false);
//  If the memory limit is 1000, then the 1001st item will be put only to datastore.
        properties.put("memory-limit", 100);
//  When the queue is initialized, items are loaded from QueueStore By default, bulk-load is 250.
        properties.put("bulk-load", 50);

        queueStoreConfig.setProperties(properties);

        queueConfig.setQueueStoreConfig(queueStoreConfig);

        return queueConfig;
    }

    @Override
    public void delete(Long key) {
        System.out.println("delete");
    }

    @Override
    public void store(Long key, SomeData value) {
        System.out.println("store");
    }

    @Override
    public void storeAll(Map<Long, SomeData> map) {
        System.out.println("store all");
    }

    @Override
    public void deleteAll(Collection<Long> keys) {
        System.out.println("deleteAll");
    }

    @Override
    public SomeData load(Long key) {
        System.out.println("load");
        return null;
    }

    @Override
    public Map<Long, SomeData> loadAll(Collection<Long> keys) {
        System.out.println("loadAll");
        return null;
    }

    @Override
    public Set<Long> loadAllKeys() {
        System.out.println("loadAllKeys");
        return null;
    }
}