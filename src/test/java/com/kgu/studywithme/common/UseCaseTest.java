package com.kgu.studywithme.common;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("UseCase")
@ExecuteParallel
@ExtendWith(MockitoExtension.class)
public abstract class UseCaseTest {
}
