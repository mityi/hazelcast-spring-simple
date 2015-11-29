package rda.hazelcast.data.map;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import rda.hazelcast.SomeData;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class BootMapEviction {
    public static final String NAME = BootMapEviction.class.getCanonicalName();

    static HazelcastInstance hazelcastInstance;

    static IMap<String, SomeData> init() {
        MapConfig mapConfig = new MapConfig();
        mapConfig.setName(NAME);

        mapConfig.setTimeToLiveSeconds(7);
        mapConfig.setMaxIdleSeconds(3);

        //    LRU: Least Recently Used.
        //    LFU: Least Frequently Used.
        mapConfig.setEvictionPolicy(EvictionPolicy.LFU);
        MaxSizeConfig maxSizeConfig = new MaxSizeConfig();
        maxSizeConfig.setSize(10);
        mapConfig.setMaxSizeConfig(maxSizeConfig);
        mapConfig.setEvictionPercentage(25/*default*/);
        //Setting it to 0 (zero) makes the eviction process run for every put operation.
        mapConfig.setMinEvictionCheckMillis(100/*default*/);


        Config config = new Config();
        config.addMapConfig(mapConfig);

        hazelcastInstance = Hazelcast.newHazelcastInstance(config);

        return hazelcastInstance.getMap(NAME);
    }

    public static void main(String[] args)  {
        IMap<String, SomeData> map = init();

        for (int i = 0; i < 20; i++) {
            map.put(String.valueOf(i), new SomeData(String.valueOf(i)));
        }

        CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
                System.out.println(map.size());
                TimeUnit.SECONDS.sleep(5);
                System.out.println(map.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println(map.size());


    }

}


