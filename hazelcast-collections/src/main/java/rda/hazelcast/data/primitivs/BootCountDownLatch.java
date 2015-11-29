package rda.hazelcast.data.primitivs;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICountDownLatch;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class BootCountDownLatch {

    public static void main(String[] args) throws Exception {

        CompletableFuture<Void> timOut = new CompletableFuture<>();
        CompletableFuture<Void> p = CompletableFuture.runAsync(() -> {

            try {
                HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
                ICountDownLatch latch = hazelcastInstance.getCountDownLatch( "countDownLatch" );
                System.out.println( "Waiting" );
                latch.trySetCount(1);
                timOut.complete(null);
                boolean success = latch.await( 10, TimeUnit.MINUTES );
                System.out.println( "Complete: " + success );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        timOut.thenRunAsync(() -> {
            try {
                HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
                ICountDownLatch latch = hazelcastInstance.getCountDownLatch("countDownLatch");
                System.out.println("Starting");
                Thread.sleep(10000);
                System.out.println("Finished");
                latch.countDown();
                latch.destroy();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        CompletableFuture.allOf(p).get();

    }

}
