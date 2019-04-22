package io.renren.modules.generator.cache;

import com.google.common.cache.CacheBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager localCacheManager() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();

        //feed缓存1小时
        GuavaCache ONE_DAY = new GuavaCache(
                "feedList",
                CacheBuilder
                        .newBuilder()
                        .expireAfterWrite(1, TimeUnit.MINUTES)
                        .build()
        );

        simpleCacheManager.setCaches(Arrays.asList(ONE_DAY));
        return simpleCacheManager;
    }
}
