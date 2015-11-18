package rda.hazelcast;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Service
public class SimpleService {
    private ConcurrentMap<String, Integer> repository = new ConcurrentHashMap<>();

    @Cacheable(value = CachingConfig.CACH_NAME, unless="#result == null")
    public Integer getWithCache(String set) {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ignore) {
        }
        return repository.get(set);
    }

    public Integer put(String set) {
        Integer integer = 0;
        if (repository.containsKey(set)) {
            integer = repository.get(set) + 1;
        }
        repository.put(set, integer);
        return integer;
    }

    public Integer get(String set) {
        return repository.get(set);
    }

    @CacheEvict(value = CachingConfig.CACH_NAME, allEntries = true)
    public void evict() {
    }
}
