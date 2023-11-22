package com.kgu.studywithme.common;

import com.kgu.studywithme.common.config.DatabaseCleanerEachCallbackExtension;
import com.kgu.studywithme.common.config.MySqlTestContainersExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@Tag("Integrate")
@SpringBootTest
@ExtendWith({
        DatabaseCleanerEachCallbackExtension.class,
        MySqlTestContainersExtension.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class IntegrateTest {
}
