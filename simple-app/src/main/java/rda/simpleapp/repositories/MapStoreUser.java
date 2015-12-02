package rda.simpleapp.repositories;

import com.hazelcast.core.MapStore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class MapStoreUser implements MapStore<String, UserDetails> {

    private ConcurrentMap<String, String> store = new ConcurrentHashMap<>(AuthHelper.devUsers());

    public void delete(String key) {
        store.remove(key);
    }

    public void store(String key, UserDetails value) {
        store.put(key, value.getUsername());
    }

    public void storeAll(Map<String, UserDetails> map) {
        for (Map.Entry<String, UserDetails> entry : map.entrySet())
            store(entry.getKey(), entry.getValue());
    }

    public void deleteAll(Collection<String> keys) {
        for (String key : keys) delete(key);
    }

    public UserDetails load(String key) {
        String value = store.get(key);
        UserDetails ud = new User(value, value,
                AuthHelper.getRole(value).stream()
                        .map(r -> (GrantedAuthority) () -> r)
                        .collect(Collectors.toList()));
        return ud;
    }

    public Map<String, UserDetails> loadAll(Collection<String> keys) {
        final Map map = new HashMap();
        for (String key : keys) {
            final UserDetails value = load(key);
            map.put(key, value);
        }
        return map;
    }

    @Override
    public Set<String> loadAllKeys() {
        return store.keySet();
    }
}