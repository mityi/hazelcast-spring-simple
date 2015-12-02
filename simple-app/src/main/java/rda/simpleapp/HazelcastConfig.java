package rda.simpleapp;

import com.hazelcast.config.Config;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;
import rda.simpleapp.repositories.MapStoreUser;

@EnableHazelcastHttpSession
public class HazelcastConfig {

    public static final String AUTH_USER_MAP = "users";
    public static final String FS_MAP = "files:data";
    public static final String FS_ID = "files:id";
    public static final String SHUTDOWN_TOPIC = "shutdown:event";
    public static final String EXEC = "exec:worker";

    @Bean(destroyMethod = "shutdown")
    public HazelcastInstance embeddedHazelcast() {
        ExecutorConfig executorConfig = new ExecutorConfig()
                .setName(EXEC).setPoolSize(1);
        MapConfig mapConfig = new MapConfig();
        mapConfig.setName(AUTH_USER_MAP);
        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setImplementation(//for example spring repositories
                new MapStoreUser());

        mapConfig.setMapStoreConfig(mapStoreConfig);

        Config config = new Config();
        config.addMapConfig(mapConfig);
        config.addExecutorConfig(executorConfig);

        return Hazelcast.newHazelcastInstance(config);
    }

}
/*
class A implements ServletContextListener {
	private HazelcastInstance instance;

	public void contextInitialized(ServletContextEvent sce) {
		String sessionMapName = "spring:session:sessions";
		ServletContext sc = sce.getServletContext();

		Config cfg = new Config();
		NetworkConfig netConfig = new NetworkConfig();
		netConfig.setPort(getAvailablePort());
		cfg.setNetworkConfig(netConfig);
		SerializerConfig serializer = new SerializerConfig()
			.setTypeClass(Object.class)
			.setImplementation(new ObjectStreamSerializer());
		cfg.getSerializationConfig().addSerializerConfig(serializer);
		MapConfig mc = new MapConfig();
		mc.setName(sessionMapName);
		mc.setTimeToLiveSeconds(MapSession.DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS);
		cfg.addMapConfig(mc);

		instance = Hazelcast.newHazelcastInstance(cfg);
		Map<String,ExpiringSession> sessions = instance.getMap(sessionMapName);

		SessionRepository<ExpiringSession> sessionRepository =
				new MapSessionRepository(sessions);
		SessionRepositoryFilter<ExpiringSession> filter =
				new SessionRepositoryFilter<ExpiringSession>(sessionRepository);
		Dynamic fr = sc.addFilter("springSessionFilter", filter);
		fr.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
 */