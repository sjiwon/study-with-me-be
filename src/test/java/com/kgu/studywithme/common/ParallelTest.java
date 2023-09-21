package com.kgu.studywithme.common;

import org.junit.jupiter.api.Tag;

/**
 * JUnit 병렬 테스트용 Abstract Class <br>
 * - Domain <br>
 * - Mocking Test <br><br>
 * 
 * <b>Spring Context를 띄우지 않는 테스트에 적용 (domain model, domain service, utils, ...)</b>
 */

@Tag("Parallel")
@ExecuteParallel
public abstract class ParallelTest {
}
