package com.kgu.studywithme.global.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OptimisticLockRetry {
    int maxRetry() default -1; // infinite

    boolean withInTransaction() default false;
}
