package rda.hazelcast.distributedquery;


import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.*;
import rda.hazelcast.SomeData;

import java.util.Collection;

public class BootQuery {

    public static final String NAME = BootQuery.class.getCanonicalName();

    public static void main(String[] args) {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        IMap<Integer, SomeData> map = hazelcastInstance.getMap(NAME);
//        map.addEntryListener(new MyEntryListener(), new SqlPredicate("surname=smith"), true);

        for (int i = 0; i < 10; i++) {
            SomeData someData = new SomeData("id" + i, "text:" + i);
            if (i % 2 == 0) {
                someData.setActive(true);
            }
            if (i % 4 == 0) {
                someData.setText("none");
            }
            someData.setCount(i);
            map.put(i, someData);
        }

        //Criteria API
        EntryObject e = new PredicateBuilder().getEntryObject();
        Predicate predicate = e.is("active").and(e.get("count").lessThan(3));
        Collection<SomeData> values = map.values(predicate);
        System.out.println("Criteria");
        values.forEach(s -> System.out.println(s));

        //SQL
        values = map.values(new SqlPredicate("active AND count > 3"));
        System.out.println("SQL");
        values.forEach(s -> System.out.println(s));

    }


    //Criteria API
    public Predicate example(String t, String nt, int c) {
        Predicate tp = Predicates.equal("text", t);
        Predicate cp = Predicates.lessEqual("count", c);
        Predicate ntc = Predicates.ilike("text", nt);

        Predicate not = Predicates.not(ntc);
        Predicate and = Predicates.and(tp, cp);
        Predicate or = Predicates.or(and, not);
        return or;
    }
    /*
QueryCacheConfig queryCacheConfig = new QueryCacheConfig("cache-name");
queryCacheConfig.getPredicateConfig().setImplementation(new OddKeysPredicate());

MapConfig mapConfig = new MapConfig("map-name");
mapConfig.addQueryCacheConfig(queryCacheConfig);

Config config = new Config();
config.addMapConfig(mapConfig);

HazelcastInstance node = Hazelcast.newHazelcastInstance(config);
IEnterpriseMap<Integer, String> map = (IEnterpriseMap) node.getMap("map-name");

QueryCache<Integer, String> cache = map.getQueryCache("cache-name");
     */

}
