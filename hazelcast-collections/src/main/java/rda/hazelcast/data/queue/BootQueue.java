package rda.hazelcast.data.queue;


import com.hazelcast.config.Config;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import rda.hazelcast.SomeData;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class BootQueue {

    private static Config getConfig() {
        Config config = new Config();
        QueueConfig queueConfig = PersistencePrintlnQueueStore.queueConfigWithStory(getSimpleName());
        // ----------- Bounded ----------------
        queueConfig.setMaxSize(10);
//  Used to purge unused or empty queues.
//  Your queue will be destroyed if it stays empty or unused for that time in seconds.
        queueConfig.setEmptyQueueTtl(100);
        config.addQueueConfig(queueConfig);
        return config;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        Config config = getConfig();

        CompletableFuture<Void> p = CompletableFuture.runAsync(() -> {
            try {
                producerMember(config);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        CompletableFuture<Void> c = CompletableFuture.runAsync(() -> {
            try {
                consumerMember(config);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        CompletableFuture.allOf(p, c).get();

    }

    private static void producerMember(Config config) throws InterruptedException {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        IQueue<SomeData> queue = hazelcastInstance.getQueue(getSimpleName());
        for (int k = 1; k < 200; k++) {
            queue.put(new SomeData(k + "-id"));
            System.out.println("Producing: " + k);
            Thread.sleep(1000);
        }
        queue.put(new SomeData("none"));
        System.out.println("Producer Finished!");
    }

    private static void consumerMember(Config config) throws InterruptedException {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        IQueue<SomeData> queue = hazelcastInstance.getQueue(getSimpleName());
        while (true) {
            SomeData item = queue.take();
            System.out.println("Consumed: " + item);
            if (item.getId().equals("none")) {
                queue.put(new SomeData("none"));
                break;
            }
            Thread.sleep(2000);
        }
        System.out.println("Consumer Finished!");
    }

    private static String getSimpleName() {
        return BootQueue.class.getSimpleName();
    }
}
