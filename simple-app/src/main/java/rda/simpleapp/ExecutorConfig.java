package rda.simpleapp;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorConfig {

    @Autowired
    HazelcastInstance hazelcastInstance;

    @Bean
    public IExecutorService executorService() {
        return hazelcastInstance.getExecutorService(HazelcastConfig.EXEC);
    }


}
