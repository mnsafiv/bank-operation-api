package ru.safonoviv.bankoperationapi.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.time.Duration;

import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration("userCache",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(30)))
                .withCacheConfiguration("userContactCache",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(30)))
                .withCacheConfiguration("clientAccountCache",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(30)));
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(1))
                .disableCachingNullValues()
                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

}
