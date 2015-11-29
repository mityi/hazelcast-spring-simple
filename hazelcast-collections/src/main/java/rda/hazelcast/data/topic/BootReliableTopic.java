package rda.hazelcast.data.topic;

import com.hazelcast.core.*;
import rda.hazelcast.SomeData;

public class BootReliableTopic implements MessageListener<SomeData> {

    public static void main(String[] args) {
        BootReliableTopic sample = new BootReliableTopic();
// To configure the ringbuffer for a reliable topic, define a ringbuffer in the config with exactly the same name.
// It is very unlikely that you want to run with the default settings.
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();

        ITopic<SomeData> topic = hazelcastInstance.getReliableTopic("default");
        topic.addMessageListener(sample);
        topic.publish(new SomeData("r", "oot"));
    }

    @Override
    public void onMessage(Message<SomeData> message) {
        System.out.println("Message received = "
                + message.getMessageObject() + " time = "
                + message.getPublishTime() + " member= "
                + message.getPublishingMember());
    }
}