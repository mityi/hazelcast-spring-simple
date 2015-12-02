package rda.simpleapp;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Set;

@Service
public class UserDetailsServiceHz implements UserDetailsService {

    @Autowired
    HazelcastInstance hazelcastInstance;

    IMap<String, UserDetails> users;

    @PostConstruct
    void init() {
        users = hazelcastInstance.getMap(HazelcastConfig.AUTH_USER_MAP);
        users.loadAll(true);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return users.get(username);
    }

    public Set<String> getAllNames() {
        return users.keySet();
    }

}
