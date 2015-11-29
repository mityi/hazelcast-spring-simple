package rda.hazelcast.distributedquery;


import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.aggregation.Aggregations;
import com.hazelcast.mapreduce.aggregation.PropertyExtractor;
import com.hazelcast.mapreduce.aggregation.Supplier;

import java.util.Random;

public class BootintAggregators {

    public static final String NAME = BootintAggregators.class.getCanonicalName();

    public static void main(String[] args) {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        IMap<String, Integer> personAgeMapping = hazelcastInstance.getMap("person-age");
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            String lastName = "LastName" + i;
            int age = random.nextInt(80);
            personAgeMapping.put(lastName, Integer.valueOf(age));
        }

        Long count = personAgeMapping.aggregate(Supplier.all(), Aggregations.count());
        System.out.println(count);


        count = personAgeMapping.aggregate(Supplier.all(new PropertyExtractor<Integer, Object>() {
            @Override
            public Object extract(Integer value) {
                if (value > 20) {
                    return null;
                }
                return value;
            }
        }), Aggregations.count());
        System.out.println(count);

        count = personAgeMapping.aggregate(Supplier.fromKeyPredicate(
                lastName -> "LastName25".equalsIgnoreCase(lastName)), Aggregations.count());
        System.out.println(count);
    }

}
