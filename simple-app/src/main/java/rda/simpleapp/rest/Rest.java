package rda.simpleapp.rest;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rda.simpleapp.HazelcastConfig;

import javax.annotation.PostConstruct;

@RestController
public class Rest {

    private ITopic<String> topic;

    @Autowired
    HazelcastInstance hazelcastInstance;

    @Autowired
    private ConfigurableApplicationContext ctx;

    @PostConstruct
    void init() {
        topic = hazelcastInstance.getReliableTopic(HazelcastConfig.SHUTDOWN_TOPIC);
        topic.addMessageListener(msg -> ctx.close());

    }

    @RequestMapping("/")
    public String hello() {
        return "Simple app";

    }

    @RequestMapping("/stopapp")
    public void stop() {

        topic.publish("sorry");

    }

}
