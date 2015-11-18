package rda.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CachingConfig {

    public static final String CACH_NAME = "hz";

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config cfg = new Config();
        HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);
        return instance;
    }

    @Bean
    public CacheManager cacheManager(HazelcastInstance instance) {
        HazelcastCacheManager hazelcastCacheManager = new HazelcastCacheManager(instance);
        Cache cache = hazelcastCacheManager.getCache(CACH_NAME);
        IMap<Object, Object> map  = (IMap<Object, Object>) cache.getNativeCache();
        map.evictAll();
        return hazelcastCacheManager;
    }


}
