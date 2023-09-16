package com.kgu.studywithme.global;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class BaseDomainEvent {
    private final LocalDateTime eventPublishedDate = LocalDateTime.now();
}
