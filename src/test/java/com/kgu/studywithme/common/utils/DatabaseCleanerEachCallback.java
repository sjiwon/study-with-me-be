package com.kgu.studywithme.common.utils;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DatabaseCleanerEachCallback implements AfterEachCallback {
    @Override
    public void afterEach(final ExtensionContext context) {
        final DatabaseCleaner databaseCleaner = SpringExtension.getApplicationContext(context).getBean(DatabaseCleaner.class);
        databaseCleaner.cleanUpDatabase();
    }
}
