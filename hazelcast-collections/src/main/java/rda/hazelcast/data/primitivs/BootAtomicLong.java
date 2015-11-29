package rda.hazelcast.data.primitivs;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IFunction;

/**
 * Created by fxf on 27.11.15.
 */
public class BootAtomicLong {
    public static class Add2Function implements IFunction<Long, Long> {
        @Override
        public Long apply(Long input) {
            return input + 2;
        }
    }

    public static void main(String[] args) {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        IAtomicLong atomicLong = hazelcastInstance.getAtomicLong("counter");
        atomicLong.incrementAndGet();

        atomicLong.set(1);
        long result = atomicLong.apply(new Add2Function());
        System.out.println("apply.result: " + result);
        System.out.println("apply.value: " + atomicLong.get());

        atomicLong.set(1);
        atomicLong.alter(new Add2Function());
        System.out.println("alter.value: " + atomicLong.get());

        atomicLong.set(1);
        result = atomicLong.alterAndGet(new Add2Function());
        System.out.println("alterAndGet.result: " + result);
        System.out.println("alterAndGet.value: " + atomicLong.get());

        atomicLong.set(1);
        result = atomicLong.getAndAlter(new Add2Function());
        System.out.println("getAndAlter.result: " + result);
        System.out.println("getAndAlter.value: " + atomicLong.get());
    }
}
