package rda.hazelcast.data.primitivs;


import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicReference;
import rda.hazelcast.SomeData;

public class BootAtomicReference {
    public static void main(String[] args) {
        Config config = new Config();
        HazelcastInstance hz = Hazelcast.newHazelcastInstance(config);

        IAtomicReference<SomeData> ref = hz.getAtomicReference("reference");
        ref.set(new SomeData("i", "d"));

        System.out.println(ref.get());
        System.exit(0);
    }

}
