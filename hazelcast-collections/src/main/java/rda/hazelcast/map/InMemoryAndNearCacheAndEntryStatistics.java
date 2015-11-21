package rda.hazelcast.map;

import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.core.EntryView;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class InMemoryAndNearCacheAndEntryStatistics {

    public static void main(String[] args) {
        /*
    BINARY (default): This is the default option.
     The data will be stored in serialized binary format.
     You can use this option if you mostly perform regular map operations,
        such as put and get.

    OBJECT: The data will be stored in deserialized form.
     This configuration is good for maps where entry processing
        and queries form the majority of all operations and the objects are complex ones,
        making the serialization cost respectively high.
     By storing objects, entry processing will not contain the deserialization cost.
         */

        Config config = new Config();

        MapConfig mapConfig = new MapConfig();
        mapConfig.setName("oo0");
        mapConfig.setInMemoryFormat(InMemoryFormat.OBJECT);
        mapConfig.setNearCacheConfig(new NearCacheConfig());
        /*  Each map.get(k)
            <near-cache>
              <max-size>5000</max-size>
              <time-to-live-seconds>0</time-to-live-seconds>
              <max-idle-seconds>60</max-idle-seconds>
              <eviction-policy>LRU</eviction-policy>
              <invalidate-on-change>true</invalidate-on-change>
              <cache-local-entries>false</cache-local-entries>
            </near-cache>
problems?
    JVM will have to hold extra cached data so it will increase the memory consumption.
    If invalidation is turned on and entries are updated frequently, then invalidations will be costly.
    Near cache breaks the strong consistency guarantees; you might be reading stale data.

         */
        config.addMapConfig(mapConfig);

        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);

        // ************   Entry Statistics    ***********

        IMap<String, String> oo0 = hazelcastInstance.getMap("oo0");
        oo0.put("1", "1");

        EntryView entry = oo0.getEntryView("1");
        System.out.println("size in memory  : " + entry.getCost());
        System.out.println("creationTime    : " + entry.getCreationTime());
        System.out.println("expirationTime  : " + entry.getExpirationTime());
        System.out.println("number of hits  : " + entry.getHits());
        System.out.println("lastAccessedTime: " + entry.getLastAccessTime());
        System.out.println("lastUpdateTime  : " + entry.getLastUpdateTime());
        System.out.println("version         : " + entry.getVersion());
        System.out.println("key             : " + entry.getKey());
        System.out.println("value           : " + entry.getValue());

        oo0.replace("1", "1", "2");

        entry = oo0.getEntryView("1");
        System.out.println("size in memory  : " + entry.getCost());
        System.out.println("creationTime    : " + entry.getCreationTime());
        System.out.println("expirationTime  : " + entry.getExpirationTime());
        System.out.println("number of hits  : " + entry.getHits());
        System.out.println("lastAccessedTime: " + entry.getLastAccessTime());
        System.out.println("lastUpdateTime  : " + entry.getLastUpdateTime());
        System.out.println("version         : " + entry.getVersion());
        System.out.println("key             : " + entry.getKey());
        System.out.println("value           : " + entry.getValue());
    }
}
