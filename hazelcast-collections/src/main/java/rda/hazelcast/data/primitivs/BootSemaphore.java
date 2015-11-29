package rda.hazelcast.data.primitivs;


import com.hazelcast.config.Config;
import com.hazelcast.config.SemaphoreConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.ISemaphore;

public class BootSemaphore {
    public static void main(String[] args) throws Exception {
        Config c = new Config();
        SemaphoreConfig semaphoreConfig = new SemaphoreConfig();
        semaphoreConfig.setName("semaphore");
        semaphoreConfig.setInitialPermits(2);
        c.addSemaphoreConfig(semaphoreConfig);

        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(c);
        ISemaphore semaphore = hazelcastInstance.getSemaphore("semaphore");
        IAtomicLong resource = hazelcastInstance.getAtomicLong("resource");
        for (int k = 0; k < 1000; k++) {
            System.out.println("At iteration: " + k + ", Active Threads: " + resource.get());
            semaphore.acquire();
            try {
                resource.incrementAndGet();
                Thread.sleep(1000);
                resource.decrementAndGet();
            } finally {
                semaphore.release();
            }
        }
        System.out.println("Finished");
    }
}

