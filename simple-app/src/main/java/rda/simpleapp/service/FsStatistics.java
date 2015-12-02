package rda.simpleapp.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rda.simpleapp.HazelcastConfig;
import rda.simpleapp.model.FsData;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class FsStatistics {
    @Autowired
    private HazelcastInstance hazelcastInstance;
    IMap<Long, FsData> virtualFs;
    JobTracker jobTracker;

    @PostConstruct
    void init() throws IOException {
        virtualFs = hazelcastInstance.getMap(HazelcastConfig.FS_MAP);
        jobTracker = hazelcastInstance.getJobTracker(HazelcastConfig.FS_MAP + ":tracer");
    }

    public ICompletableFuture<Map<String, String>> get() {

        KeyValueSource<Long, FsData> source = KeyValueSource.fromMap(virtualFs);
        Job<Long, FsData> job = jobTracker.newJob(source);

        ICompletableFuture<Map<String, String>> future = job
                .mapper(new MyLifecycleMapperAdapter())
                .combiner(new MyCombinerFactory())
                .reducer(new MyReducerFactory())
                .submit();
        return future;
    }

    public static class MyLifecycleMapperAdapter extends LifecycleMapperAdapter<Long, FsData, String, FsData> {
        @Override
        public void map(Long key, FsData value, Context<String, FsData> context) {
            context.emit(value.getName(), value);
        }
    }

    public static class MyCombinerFactory implements CombinerFactory<String, FsData, Set<String>> {

        @Override
        public Combiner<FsData, Set<String>> newCombiner(String key) {
            return new TmpCombiner();
        }

        private class TmpCombiner extends Combiner<FsData, Set<String>> {
            Set<String> list = new HashSet<String>();

            @Override
            public void combine(FsData value) {
                list.add(value.getNode());
            }

            @Override
            public Set<String> finalizeChunk() {
                return list;
            }
        }

    }

    public static class MyReducerFactory implements ReducerFactory<String, Set<String>, String> {

        @Override
        public Reducer<Set<String>, String> newReducer(String key) {
            return new TmpReducer();
        }

        private class TmpReducer extends Reducer<Set<String>, String> {
            Set<String> data = new HashSet<String>();

            @Override
            public void reduce(Set<String> value) {
                data.addAll(value);
            }

            @Override
            public String finalizeReduce() {
                return Arrays.deepToString(data.toArray());
            }
        }
    }
}
