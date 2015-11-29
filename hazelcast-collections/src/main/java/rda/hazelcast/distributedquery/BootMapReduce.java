package rda.hazelcast.distributedquery;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.*;

import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

/**
 * Created by fxf on 29.11.15.
 */
public class BootMapReduce {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();

        JobTracker jobTracker = hazelcastInstance.getJobTracker("default");

        IMap<String, String> map = hazelcastInstance.getMap("articles");

        map.put("f", "FGH");
        map.put("g", "GH");
        map.put("h", "H");
        map.put("hh", "H");
        map.put("hhh", "H");

        KeyValueSource<String, String> source = KeyValueSource.fromMap(map);
        Job<String, String> job = jobTracker.newJob(source);

        ICompletableFuture<Map<String, Long>> future = job
                .mapper(new TokenizerMapper())
                .combiner(new WordCountCombinerFactory())
                .reducer(new WordCountReducerFactory())
                .submit();

// Attach a callback listener
//        future.andThen(buildCallback());

// Wait and retrieve the result
        Map<String, Long> result = future.get();
        result.entrySet().forEach(e -> System.out.println(e.getKey() + " - " + e.getValue()));
    }

    public static class TokenizerMapper implements Mapper<String, String, String, Long> {

        @Override
        public void map(String key, String document, Context<String, Long> context) {
            StringTokenizer tokenizer = new StringTokenizer(document.toLowerCase());
            while (tokenizer.hasMoreTokens()) {
                context.emit(tokenizer.nextToken(), (long) document.toCharArray().length);
            }
        }
    }

    public static class WordCountCombinerFactory
            implements CombinerFactory<String, Long, Long> {

        @Override
        public Combiner<Long, Long> newCombiner(String key) {
            return new WordCountCombiner();
        }

        private class WordCountCombiner extends Combiner<Long, Long> {
            private long sum = 0;

            @Override
            public void combine(Long value) {
                sum++;
            }

            @Override
            public Long finalizeChunk() {
                return sum;
            }

            @Override
            public void reset() {
                sum = 0;
            }
        }
    }

    public static class WordCountReducerFactory implements ReducerFactory<String, Long, Long> {

        @Override
        public Reducer<Long, Long> newReducer(String key) {
            return new WordCountReducer();
        }

        private class WordCountReducer extends Reducer<Long, Long> {
            private volatile long sum = 0;

            @Override
            public void reduce(Long value) {
                sum += value.longValue();
            }

            @Override
            public Long finalizeReduce() {
                return sum;
            }
        }
    }

}
