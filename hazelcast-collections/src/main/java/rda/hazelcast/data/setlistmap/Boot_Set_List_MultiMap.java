package rda.hazelcast.data.setlistmap;

import com.hazelcast.core.*;
import rda.hazelcast.SomeData;

import java.util.Collection;
import java.util.Iterator;

public class Boot_Set_List_MultiMap implements ItemListener<SomeData> {

    public static void main(String[] args) {
//        SetConfig setConfig = new SetConfig();
//        ListConfig listConfig = new ListConfig();
//        MultiMapConfig multiMapConfig = new MultiMapConfig()
//                .addEntryListenerConfig()
//                .setValueCollectionType(MultiMapConfig.ValueCollectionType.LIST);
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();

        Boot_Set_List_MultiMap sample = new Boot_Set_List_MultiMap();

        set(sample, hazelcastInstance);
        list(sample, hazelcastInstance);

        System.out.print("-------------------- MM ---------------------");

        MultiMap<String, String> map = hazelcastInstance.getMultiMap("map");
        map.put("a", "1");
        map.put("a", "2");
        map.put("b", "3");
        System.out.println("PutMember:Done");
        for (String key : map.keySet()) {
            Collection<String> values = map.get(key);
            System.out.printf("%s -> %s\n", key, values);
        }
    }

    private static void set(Boot_Set_List_MultiMap sample, HazelcastInstance hazelcastInstance) {
        System.out.print("--------------------SET ---------------------");
        ISet<SomeData> set = hazelcastInstance.getSet("defaultS");
        set.addItemListener(sample, true);

        SomeData price = new SomeData("1SET", "1qaz");
        set.add(price);
        set.add(price);
        System.out.println("S" + set.size());
        //return Collections.unmodifiableCollection(getAll()).iterator();
        Iterator<SomeData> iterator = set.iterator();
        while (iterator.hasNext()) {
            SomeData data = iterator.next();
            //analyze
            System.out.println(data);
        }
        set.remove(price);
    }

    private static void list(Boot_Set_List_MultiMap sample, HazelcastInstance hazelcastInstance) {
        System.out.print("-------------------- LIST ---------------------");
        IList<SomeData> list = hazelcastInstance.getList("defaultL");
        list.addItemListener(sample, true);

        SomeData price = new SomeData("1List", "1qaz");
        list.add(price);
        list.add(price);
        System.out.println("L" + list.size());
        //return Collections.unmodifiableCollection(getAll()).iterator();
        Iterator<SomeData> iterator = list.iterator();
        while (iterator.hasNext()) {
            SomeData data = iterator.next();
            //analyze
            System.out.println(data);
        }
        list.remove(price);
        System.out.println("L" + list.size());
    }

    @Override
    public void itemAdded(ItemEvent<SomeData> item) {
        System.out.println("Item added = " + item);
    }

    @Override
    public void itemRemoved(ItemEvent<SomeData> item) {
        System.out.println("Item removed = " + item);
    }
}