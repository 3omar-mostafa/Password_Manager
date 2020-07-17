package com.hafez.password_manager.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.OnConflictStrategy;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.hafez.password_manager.R;
import com.hafez.password_manager.LiveDataUtils;
import com.hafez.password_manager.RetryFailedTestsRule;
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

    @Rule(order = Integer.MIN_VALUE)
    public RetryFailedTestsRule retryFailedTestsRule = new RetryFailedTestsRule(5);

    // Allows Android Architecture Components (ex. Live Data) to executes each task synchronously.
    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    private AppDatabase database;
    private LoginInfoDao dao;

    private List<LoginInfo> sampleData = new ArrayList<>();

    @Before
    public void initDatabase() {
        database = DatabaseTestUtils.getInMemoryDatabase();
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
    public void getLoginInfoListTest() {
        LiveData<List<LoginInfo>> liveData = dao.getLoginInfoList();

        List<LoginInfo> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertNotNull(loginInfoList);
        assertFalse(loginInfoList.isEmpty());
        assertEquals(loginInfoList, sampleData);
    }


    @Test
    public void insertTest() {
        LoginInfo loginInfo = new LoginInfo("user_1", "pass_1", R.drawable.ic_launcher);

        dao.insert(loginInfo);

        LiveData<List<LoginInfo>> liveData = dao.getLoginInfoList();

        List<LoginInfo> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertNotNull(loginInfoList);
        assertFalse(loginInfoList.isEmpty());
        assertEquals(loginInfoList.size(), sampleData.size() + 1);
        loginInfo.setId(sampleData.size() + 1);
        assertTrue(loginInfoList.contains(loginInfo));
    }


    /**
     * According to using {@link OnConflictStrategy#REPLACE}
     */
    @Test
    public void insertExistingItemTest() {
        LoginInfo loginInfo = sampleData.get(0);

        dao.insert(loginInfo);

        LiveData<List<LoginInfo>> liveData = dao.getLoginInfoList();

        List<LoginInfo> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertNotNull(loginInfoList);
        assertFalse(loginInfoList.isEmpty());
        assertEquals(loginInfoList.size(), sampleData.size());
        assertTrue(loginInfoList.contains(loginInfo));
    }


    @Test
    public void updateTest() {
        int firstElement = 0;

        LoginInfo newData = sampleData.get(firstElement);
        newData.setUsername("new_name");
        newData.setPassword("new_password");

        dao.update(newData);

        LiveData<List<LoginInfo>> liveData = dao.getLoginInfoList();

        List<LoginInfo> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertNotNull(loginInfoList);
        assertFalse(loginInfoList.isEmpty());
        assertEquals(loginInfoList.size(), sampleData.size());

        LoginInfo loginInfo = loginInfoList.get(firstElement);
        assertEquals(newData, loginInfo);
    }

    @Test
    public void updateNotExistingItemTest() {
        LoginInfo newData = new LoginInfo("new_name", "new_password", 0);
        newData.setId(sampleData.size() + 1);

        dao.update(newData);

        LiveData<List<LoginInfo>> liveData = dao.getLoginInfoList();

        List<LoginInfo> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertNotNull(loginInfoList);
        assertFalse(loginInfoList.isEmpty());
        assertEquals(loginInfoList.size(), sampleData.size());
        assertEquals(sampleData, loginInfoList);
    }


    @Test
    public void deleteTest() {
        int firstElement = 0;

        dao.delete(sampleData.get(firstElement));

        LiveData<List<LoginInfo>> liveData = dao.getLoginInfoList();

        List<LoginInfo> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertNotNull(loginInfoList);
        assertFalse(loginInfoList.isEmpty());
        assertFalse(loginInfoList.contains(sampleData.get(firstElement)));
    }


    @Test
    public void deleteNotExistingItemTest() {
        LoginInfo toBeDeleted = new LoginInfo("new_name", "new_password", 0);
        toBeDeleted.setId(sampleData.size() + 1);

        dao.delete(toBeDeleted);

        LiveData<List<LoginInfo>> liveData = dao.getLoginInfoList();

        List<LoginInfo> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertNotNull(loginInfoList);
        assertFalse(loginInfoList.isEmpty());
        assertEquals(sampleData, loginInfoList);
    }


    @Test
    public void deleteAllTest() {
        dao.deleteAllLoginInfo();

        LiveData<List<LoginInfo>> liveData = dao.getLoginInfoList();

        List<LoginInfo> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertNotNull(loginInfoList);
        assertTrue(loginInfoList.isEmpty());
    }


    @Test
    public void getLoginInfoTest() {
        int firstItem = 0;
        LoginInfo expectedLoginInfo = sampleData.get(firstItem);

        LiveData<LoginInfo> liveData = dao.getLoginInfo(expectedLoginInfo.getId());

        LoginInfo loginInfo = LiveDataUtils.getValueOf(liveData);

        assertNotNull(loginInfo);
        assertEquals(expectedLoginInfo, loginInfo);
    }


    @Test
    public void getNonExistingLoginInfoTest() {
        LiveData<LoginInfo> liveData = dao.getLoginInfo(sampleData.size() + 1);

        LoginInfo loginInfo = LiveDataUtils.getValueOf(liveData);

        assertNull(loginInfo);
    }

}