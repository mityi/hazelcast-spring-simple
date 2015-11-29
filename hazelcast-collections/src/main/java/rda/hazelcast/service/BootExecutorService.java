package rda.hazelcast.service;


import com.hazelcast.config.Config;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.MemberAttributeConfig;
import com.hazelcast.core.*;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class BootExecutorService {
    public static void main(String[] args) throws Exception {
        ExecutorConfig executorConfig = new ExecutorConfig()
                .setName("exec")
                .setPoolSize(1);

        Config config = new Config().addExecutorConfig(executorConfig);

        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        IExecutorService executor = hazelcastInstance.getExecutorService("exec");
        executor.submit(new Echo("EEE"), new ExecutionCallback<String>() {

            @Override
            public void onResponse(String response) {
                System.out.println(response);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
        for (int k = 1; k <= 1000; k++) {
            Thread.sleep(1000);
            System.out.println("Producing echo task: " + k);
            executor.execute(new EchoTask(String.valueOf(k)));
        }
        System.out.println("EchoTaskMain finished!");
    }

    /**
     * Example Method to Execute and Cancel the Task
     */
    boolean exampleMethodToExecuteAndCancelTheTask(String n, HazelcastInstance hazelcastInstance)
            throws ExecutionException, InterruptedException {
        IExecutorService es = hazelcastInstance.getExecutorService("default");
        Future future = es.submit(new EchoTask(n));
        try {
            future.get(3, TimeUnit.SECONDS);
            return true;
        } catch (TimeoutException e) {
            future.cancel(true);
        }
        return false;
    }

    /**
     * execute examples
     */
    public void echoOnTheMember(String input, Member member) throws Exception {
        Callable<String> task = new Echo(input);
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        IExecutorService executorService =
                hazelcastInstance.getExecutorService("default");

        Future<String> future = executorService.submitToMember(task, member);
        String echoResult = future.get();
    }

    public void echoOnTheMemberOwningTheKey(String input, Object key) throws Exception {
        Callable<String> task = new Echo(input);
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        IExecutorService executorService =
                hazelcastInstance.getExecutorService("default");

        Future<String> future = executorService.submitToKeyOwner(task, key);
        String echoResult = future.get();
    }

    public void echoOnSomewhere(String input) throws Exception {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        IExecutorService executorService =
                hazelcastInstance.getExecutorService("default");

        Future<String> future = executorService.submit(new Echo(input));
        String echoResult = future.get();
    }

    public void echoOnMembers(String input, Set<Member> members) throws Exception {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        IExecutorService executorService =
                hazelcastInstance.getExecutorService("default");

        Map<Member, Future<String>> futures = executorService
                .submitToMembers(new Echo(input), members);

        for (Future<String> future : futures.values()) {
            String echoResult = future.get();
            // ...
        }
    }

    /**
     * Execution Member Selection
     */
    public static class MyMemberSelector implements MemberSelector {
        public static Config getConfigWithSelector() {
            Config config = new Config();
            MemberAttributeConfig memberAttributeConfig = new MemberAttributeConfig();
            memberAttributeConfig.setBooleanAttribute("my.special.executor", true);
            config.setMemberAttributeConfig(memberAttributeConfig);
            return config;
        }

        public boolean select(Member member) {
            return Boolean.TRUE.equals(member.getBooleanAttribute("my.special.executor"));
        }
    }

    //**************************************************************

    public static class EchoTask implements Runnable, Serializable {

        private final String msg;

        public EchoTask(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            System.out.println("echo:" + msg);
        }

    }

    public static class Echo implements Callable<String>, Serializable {

        private final String msg;

        public Echo(String msg) {
            this.msg = msg;
        }

        @Override
        public String call() throws Exception {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            return "echo:" + msg;
        }

    }
}
