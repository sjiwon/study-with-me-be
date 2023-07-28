package com.kgu.studywithme.common;

import com.kgu.studywithme.common.config.RedisTestContainersConfiguration;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(initializers = RedisTestContainersConfiguration.Initializer.class)
public @interface RedisTestContainers {
}
