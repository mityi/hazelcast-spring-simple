package rda.hazelcast.data.topic;

import com.hazelcast.core.*;
import rda.hazelcast.SomeData;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BootTopic implements MessageListener<SomeData> {

    public static void main(String[] args) {
//        TopicConfig topicConfig = new TopicConfig();
//        topicConfig.setGlobalOrderingEnabled( true );
//        topicConfig.setStatisticsEnabled( true );
//        topicConfig.setName( "yourTopicName" );

        BootTopic sample = new BootTopic();
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        ITopic topic = hazelcastInstance.getTopic("default");
        topic.addMessageListener(sample);
        topic.publish(new SomeData("r", "oot"));
        System.out.println(
                topic.getLocalTopicStats().getPublishOperationCount() + "-" +
                        topic.getLocalTopicStats().getReceiveOperationCount());
    }

    private final Executor messageExecutor = Executors.newSingleThreadExecutor();

    @Override
    public void onMessage(Message<SomeData> message) {
//        SomeData someData = message.getMessageObject();
        messageExecutor.execute(() -> System.out.println("Message received = "
                + message.getMessageObject() + " time = "
                + message.getPublishTime() + " member= "
                + message.getPublishingMember()));

    }
}