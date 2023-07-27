package com.kgu.studywithme.common;

import com.kgu.studywithme.common.config.MySqlTestContainersConfiguration;
import com.kgu.studywithme.global.config.P6SpyConfiguration;
import com.kgu.studywithme.global.config.QueryDslConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest(showSql = false)
@Import({QueryDslConfiguration.class, P6SpyConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = MySqlTestContainersConfiguration.Initializer.class)
public abstract class RepositoryTest {
}
