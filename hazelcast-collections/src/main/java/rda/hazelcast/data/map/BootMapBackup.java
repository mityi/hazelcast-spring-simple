package rda.hazelcast.data.map;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import rda.hazelcast.SomeData;

public class BootMapBackup {

    static final String NAME = BootMapBackup.class.getCanonicalName();

    HazelcastInstance hazelcastInstance;
    IMap<String, SomeData> datas;

    public BootMapBackup() {
        MapConfig mapConfig = new MapConfig();
        mapConfig.setName(NAME);

        //Sync & Async
        mapConfig.setBackupCount(1);
        mapConfig.setAsyncBackupCount(1);

        //Enabling backup reads can improve performance.
        mapConfig.setReadBackupData(true);

        Config config = new Config();
        config.addMapConfig(mapConfig);

        hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        datas = hazelcastInstance.getMap(NAME);
    }

    public static void main(String[] args) {

        BootMapBackup bootMap = new BootMapBackup();

        SomeData first = bootMap.create("first");
        System.out.print("create " + first);

        first.setText("new");
        bootMap.updateData(first);
        first = bootMap.getData("first");
        System.out.print("update " + first);

        bootMap.removeData(first);
        first = bootMap.getData("first");
        System.out.print("remove " + first);


    }

    public SomeData create(String id) {
        SomeData data = new SomeData(id);
        data.setText(id);
        //@return a clone of the previous value.
        datas.putIfAbsent(id, data);
        return data;
    }

    public SomeData getData(String id) {

        return datas.get(id);
    }

    public boolean updateData(SomeData data) {
        return (datas.replace(data.getId(), data) != null);
    }

    public boolean removeData(SomeData data) {
        return datas.remove(data.getId(), data);
    }

}


