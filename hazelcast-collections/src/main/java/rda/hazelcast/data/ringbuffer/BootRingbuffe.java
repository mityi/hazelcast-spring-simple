package rda.hazelcast.data.ringbuffer;


import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IFunction;
import com.hazelcast.ringbuffer.OverflowPolicy;
import com.hazelcast.ringbuffer.ReadResultSet;
import com.hazelcast.ringbuffer.Ringbuffer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BootRingbuffe {
    public static void main(String[] args) throws InterruptedException, ExecutionException {

        RingbufferConfig rbConfig = new RingbufferConfig("rb")
                .setCapacity(10000)
                .setBackupCount(1)
                .setAsyncBackupCount(0)
                .setTimeToLiveSeconds(0)
                .setInMemoryFormat(InMemoryFormat.BINARY);
        Config config = new Config();
        config.addRingBufferConfig(rbConfig);

        HazelcastInstance hz = Hazelcast.newHazelcastInstance(config);
        Ringbuffer<String> ringbuffer = hz.getRingbuffer("rb");
        long sequence = ringbuffer.headSequence();

// OverflowPolicy.OVERWRITE: The oldest item is overwritten.
// OverflowPolicy.FAIL: The call is aborted.
        List<String> items = Arrays.asList("1", "2", "3", "4", "5");
        ICompletableFuture<Long> f = ringbuffer.addAllAsync(items, OverflowPolicy.OVERWRITE);
        f.get();

        IFunction<String, Boolean> filter = null; //s -> true;
        for (; ; ) {
            ICompletableFuture<ReadResultSet<String>> f2 = ringbuffer.readManyAsync(sequence, 1, 2, filter);
            ReadResultSet<String> rs = f2.get();
            for (String s : rs) {
                System.out.println(s);
            }
            sequence += rs.readCount();
        }

    }
}
