package se.vgregion.ifeedpoc.spring;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.core.config.DefaultConfiguration;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.ehcache.impl.config.persistence.DefaultPersistenceConfiguration;
import org.ehcache.jsr107.Eh107Configuration;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.ehcache.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PreDestroy;
import javax.cache.Cache;
import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author Patrik Björk
 */
@Configuration
@EnableCaching
@EnableScheduling
public class AppConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    private Cache<Object, Object> cache;

    @PreDestroy
    public void shutdown() {
        if (cache != null) {
            cache.close();
        }
    }

    @Bean
    public CacheManager cacheManager() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        EhcacheCachingProvider ehcacheCachingProvider = new EhcacheCachingProvider();

        File rootDirectory = new File(System.getProperty("java.io.tmpdir"), "handbok_ehcache");
        File lockFile = new File(rootDirectory, ".lock");
        boolean delete = lockFile.delete();

        if (!delete) {
            LOGGER.warn("Failed to delete " + lockFile.getAbsolutePath());
        }

        DefaultConfiguration configuration = new DefaultConfiguration(ehcacheCachingProvider.getDefaultClassLoader(),
                new DefaultPersistenceConfiguration(rootDirectory));
        javax.cache.CacheManager cacheManager = ehcacheCachingProvider.getCacheManager(ehcacheCachingProvider.getDefaultURI(), configuration);

        XmlConfiguration xmlConfiguration = new XmlConfiguration(getClass().getClassLoader()
                .getResource("ehcache.xml"));

        CacheConfigurationBuilder<Object, Object> newCacheConfigurationBuilderFromTemplate =
                xmlConfiguration
                        .newCacheConfigurationBuilderFromTemplate("cache-template", Object.class, Object.class)
                        .withExpiry(Expirations.timeToLiveExpiration(new Duration(24, TimeUnit.HOURS)));

        cache = cacheManager.createCache("default", Eh107Configuration
                .fromEhcacheCacheConfiguration(
                        newCacheConfigurationBuilderFromTemplate));

        cacheManager.enableStatistics("default", true);
        cacheManager.enableManagement("default", true);

        return new JCacheCacheManager(cacheManager);
    }



}
