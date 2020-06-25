package com.hafez.password_manager.database;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

public class DatabaseTestUtils {

    public static AppDatabase getInMemoryDatabase() {
        return Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
                AppDatabase.class).build();
    }

}
