package rda.simpleapp.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.query.Predicates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rda.simpleapp.HazelcastConfig;
import rda.simpleapp.model.FsData;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

@Service
public class FsService {

    @Value("${fs.path}")
    private String rootPath;
    private String node;

    @Autowired
    private HazelcastInstance hazelcastInstance;
    IMap<Long, FsData> virtualFs;
    private IdGenerator generator;

    @PostConstruct
    void init() throws IOException {
        virtualFs = hazelcastInstance.getMap(HazelcastConfig.FS_MAP);
        generator = hazelcastInstance.getIdGenerator(HazelcastConfig.FS_ID);
        node = generator.getPartitionKey() + generator.newId();
        Path root = Paths.get(rootPath);
        loadFiles(root);
    }

    @PreDestroy
    void shutdown() {
        virtualFs.keySet(Predicates.equal("node", node)).forEach(virtualFs::remove);
    }

    private void loadFiles(Path root) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            for (Path entry : stream) {
                FsData data = new FsData(entry, rootPath, node);
                virtualFs.put(generator.newId(), data);
                if (data.isDirectory()) {
                    loadFiles(entry);
                }
            }
        }
    }

    public Collection<FsData> getAll() {
        return virtualFs.values();
    }


    public Collection<FsData> getInPath(String path) {
        if (!path.endsWith("/")) {
            path += "/";
        }
        return virtualFs.values(Predicates.like("path", path + "%"));
    }

    public Collection<FsData> getByPath(String path) {
        return virtualFs.values(Predicates.equal("path", path));
    }

}
