package com.hafez.password_manager.database;

import static org.junit.Assert.assertTrue;

import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import com.hafez.password_manager.LiveDataUtils;
import com.hafez.password_manager.models.LoginInfoFull;
import com.hafez.password_manager.repositories.LoginInfoRepository;
import java.util.List;

public class DatabaseTestUtils {

    public static void assertThatLoginInfoTableIsEmpty(LoginInfoRepository repository) {
        LiveData<List<LoginInfoFull>> liveData = repository.getAllLoginInfoList();

        List<LoginInfoFull> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertTrue(loginInfoList.isEmpty());
    }

    public static long getLoginInfoTableSize(LoginInfoRepository repository) {
        LiveData<List<LoginInfoFull>> liveData = repository.getAllLoginInfoList();

        List<LoginInfoFull> loginInfoList = LiveDataUtils.getValueOf(liveData);

        return loginInfoList.size();
    }


    public static AppDatabase getInMemoryDatabase() {
        return Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
                AppDatabase.class).build();
    }

}
