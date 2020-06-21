package com.hafez.password_manager.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.hafez.password_manager.R;
import com.hafez.password_manager.models.LoginInfo;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    private AppDatabase database;
    private LoginInfoDao dao;

    private List<LoginInfo> sampleData = new ArrayList<>();

    @Before
    public void initDatabase() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
                AppDatabase.class).build();
        dao = database.getLoginInfoDao();

        sampleData.add(new LoginInfo(1, "u_1", "p_1", R.drawable.ic_launcher));
        sampleData.add(new LoginInfo(2, "u_2", "p_2", R.drawable.ic_launcher));

        for (LoginInfo loginInfo : sampleData) {
            dao.insert(loginInfo);
        }

    }

    @After
    public void closeDatabase() {
        database.close();
    }


    @Test
    public void getLoginInfoList() {
        LiveData<List<LoginInfo>> liveData = dao.getLoginInfoList();

        liveData.observeForever(new Observer<List<LoginInfo>>() {
            @Override
            public void onChanged(List<LoginInfo> loginInfoList) {
                assertNotNull(loginInfoList);
                assertFalse(loginInfoList.isEmpty());
                assertEquals(loginInfoList, sampleData);
                liveData.removeObserver(this);
            }
        });
    }


    @Test
    public void insert() {
        LoginInfo loginInfo = new LoginInfo("user_1", "pass_1", R.drawable.ic_launcher);
        loginInfo.setId(sampleData.size() + 1);

        dao.insert(loginInfo);

        LiveData<List<LoginInfo>> liveData = dao.getLoginInfoList();
        liveData.observeForever(new Observer<List<LoginInfo>>() {
            @Override
            public void onChanged(List<LoginInfo> loginInfoList) {
                assertNotNull(loginInfoList);
                assertFalse(loginInfoList.isEmpty());
                assertEquals(loginInfoList.size(), sampleData.size() + 1);
                assertTrue(loginInfoList.contains(loginInfo));
                liveData.removeObserver(this);
            }
        });
    }


    @Test
    public void update() {
        int firstElement = 0;

        LoginInfo newData = sampleData.get(firstElement);
        newData.setUsername("new_name");
        newData.setPassword("new_password");

        dao.update(newData);

        LiveData<List<LoginInfo>> liveData = dao.getLoginInfoList();
        liveData.observeForever(new Observer<List<LoginInfo>>() {
            @Override
            public void onChanged(List<LoginInfo> loginInfoList) {
                assertNotNull(loginInfoList);
                assertFalse(loginInfoList.isEmpty());
                assertEquals(loginInfoList.size(), sampleData.size());

                LoginInfo loginInfo = loginInfoList.get(firstElement);
                assertEquals(newData, loginInfo);

                liveData.removeObserver(this);
            }
        });
    }


    @Test
    public void delete() {
        int firstElement = 0;

        dao.delete(sampleData.get(firstElement));

        LiveData<List<LoginInfo>> liveData = dao.getLoginInfoList();
        liveData.observeForever(new Observer<List<LoginInfo>>() {
            @Override
            public void onChanged(List<LoginInfo> loginInfoList) {
                assertNotNull(loginInfoList);
                assertFalse(loginInfoList.isEmpty());
                assertFalse(loginInfoList.contains(sampleData.get(firstElement)));
                liveData.removeObserver(this);
            }
        });
    }


    @Test
    public void deleteAll() {
        dao.deleteAllLoginInfo();

        LiveData<List<LoginInfo>> liveData = dao.getLoginInfoList();
        liveData.observeForever(new Observer<List<LoginInfo>>() {
            @Override
            public void onChanged(List<LoginInfo> loginInfoList) {
                assertNotNull(loginInfoList);
                assertTrue(loginInfoList.isEmpty());
                liveData.removeObserver(this);
            }
        });
    }


}