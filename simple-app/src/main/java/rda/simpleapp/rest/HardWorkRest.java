package rda.simpleapp.rest;


import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.IExecutorService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

@RestController
@RequestMapping("/hardwork")
public class HardWorkRest {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(HardWorkRest.class);
    @Autowired
    IExecutorService executor;

    @RequestMapping("/")
    public Long hello() {

        for (int k = 1; k <= 20; k++) {
            work();
        }

        return -1l;

    }

    private void work() {
        executor.submit(new Mock(), new ExecutionCallback<Object>() {
            @Override
            public void onResponse(Object response) {
                //ignore
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public static class Mock implements Runnable, Serializable {

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                log.error("Work");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
