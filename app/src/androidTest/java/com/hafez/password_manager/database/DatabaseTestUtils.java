package com.hafez.password_manager.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import com.hafez.password_manager.TestObserver;
import com.hafez.password_manager.models.LoginInfo;
import com.hafez.password_manager.repositories.LoginInfoRepository;
import java.util.List;

public class DatabaseTestUtils {

    public static void assertThatLoginInfoTableIsEmpty(LoginInfoRepository repository) {

        TestObserver<List<LoginInfo>> observer = new TestObserver<List<LoginInfo>>() {
            @Override
            public void onChangedBehaviour(List<LoginInfo> loginInfoList) {
                assertEquals(0, loginInfoList.size());
            }
        };

        repository.getAllLoginInfoList().observeForever(observer);

        assertTrue(observer.isOnChangedCalled());

        repository.getAllLoginInfoList().removeObserver(observer);
    }

    public static long getLoginInfoTableSize(LoginInfoRepository repository) {

        final long[] size = {0};

        TestObserver<List<LoginInfo>> observer = new TestObserver<List<LoginInfo>>() {
            @Override
            public void onChangedBehaviour(List<LoginInfo> loginInfoList) {
                size[0] = loginInfoList.size();
            }
        };

        repository.getAllLoginInfoList().observeForever(observer);

        assertTrue(observer.isOnChangedCalled());

        repository.getAllLoginInfoList().removeObserver(observer);

        return size[0];
    }


    public static AppDatabase getInMemoryDatabase() {
        return Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
                AppDatabase.class).build();
    }

}
