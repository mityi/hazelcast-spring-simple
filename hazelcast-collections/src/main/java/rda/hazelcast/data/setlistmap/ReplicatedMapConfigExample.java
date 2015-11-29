package rda.hazelcast.data.setlistmap;

import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.ReplicatedMapConfig;

public class ReplicatedMapConfigExample {
//    it replicates the data to all nodes.

//    This leads to higher memory consumption. However,
// a replicated map has faster read and write access since the data are available on
// all nodes and writes take place on local nodes, eventually being replicated to all other nodes.

//    Weak consistency compared to eventually consistency means that replication is
// done on a best efforts basis. Lost or missing updates are neither tracked nor resent.
// This kind of data structure is suitable for immutable objects, catalogue data,
// or idempotent calculable data (like HTML pages).


    ReplicatedMapConfig replicatedMapConfig =
            new ReplicatedMapConfig().setName("default")
                    .setInMemoryFormat(InMemoryFormat.BINARY)
                    .setConcurrencyLevel(32);
}
