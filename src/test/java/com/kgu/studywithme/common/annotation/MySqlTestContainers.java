package com.kgu.studywithme.common.annotation;

import com.kgu.studywithme.common.config.MySqlTestContainersConfiguration;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(initializers = MySqlTestContainersConfiguration.Initializer.class)
public @interface MySqlTestContainers {
}
