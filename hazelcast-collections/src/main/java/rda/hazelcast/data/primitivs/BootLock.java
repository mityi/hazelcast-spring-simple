package rda.hazelcast.data.primitivs;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICondition;
import com.hazelcast.core.ILock;

import java.util.concurrent.TimeUnit;

public class BootLock {

    public static void main(String[] args) throws InterruptedException {
// ICondition is the distributed implementation of the notify, notifyAll and wait operations on the Java object.
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        ILock lock = hazelcastInstance.getLock("myLock");
        ICondition condition = lock.newCondition("myConditionId");


        lock.lock();
        try {
            // do something here
            condition.signalAll();
            condition.await();// frees the lock and waits for signal
            // when it wakes up it re-acquires the lock
            // if available or waits for it to become
            // available

        } finally {
            lock.unlock();
        }

//*
        if (lock.tryLock(10, TimeUnit.SECONDS)) {
            try {
                // do some stuff here..
            } finally {
                lock.unlock();
            }
        } else {
            // warning
        }

//*
        lock.lock(5, TimeUnit.SECONDS);
        try {
            // do some stuff here..
        } finally {
            try {
                lock.unlock();
            } catch (IllegalMonitorStateException ex) {
                // WARNING Critical section guarantee can be broken
            }
        }

    }
}
