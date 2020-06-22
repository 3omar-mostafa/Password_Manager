package com.hafez.password_manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.hafez.password_manager.mock.MockRepository;
import com.hafez.password_manager.models.LoginInfo;
import com.hafez.password_manager.repositories.LoginInfoRepository;
import com.hafez.password_manager.view_models.MainActivityViewModel;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MainActivityTest {

    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    private MainActivityViewModel viewModel;

    private List<LoginInfo> loginInfoExpectedList;
    private TestObserver observer;

    @Before
    public void init() {
        LoginInfoRepository repository = setUpMockRepository();

        viewModel = new MainActivityViewModel.Factory(repository)
                .create(MainActivityViewModel.class);
    }

    public LoginInfoRepository setUpMockRepository() {

        loginInfoExpectedList = new ArrayList<>();

        loginInfoExpectedList.add(new LoginInfo(1, "user_1", "pass_1", R.drawable.ic_launcher));
        loginInfoExpectedList.add(new LoginInfo(2, "user_2", "pass_2", R.drawable.ic_launcher));
        loginInfoExpectedList.add(new LoginInfo(3, "user_2", "pass_3", R.drawable.ic_launcher));

        return new MockRepository(loginInfoExpectedList);
    }

    @After
    public void cleanup() {
        viewModel.getLoginInfoLiveDataList().removeObserver(observer);
    }


    @Test
    public void getLoginInfoLiveDataListTest() {

        observer = new TestObserver() {
            @Override
            public void onChangedBehaviour(List<LoginInfo> loginInfoList) {
                assertNotNull(loginInfoList);
                assertFalse(loginInfoList.isEmpty());
                assertEquals(loginInfoList, loginInfoExpectedList);
            }
        };

        viewModel.getLoginInfoLiveDataList().observeForever(observer);

        assertTrue(observer.isOnChangedCalled());

    }

    @Test
    public void insertLoginInfoTest() {
        LoginInfo newData = new LoginInfo("new_user", "new_pass", 0);
        newData.setId(loginInfoExpectedList.size() + 1);

        viewModel.insertLoginInfo(newData);

        observer = new TestObserver() {
            @Override
            public void onChangedBehaviour(List<LoginInfo> loginInfoList) {
                assertNotNull(loginInfoList);
                assertFalse(loginInfoList.isEmpty());
                assertEquals(loginInfoList.size(), loginInfoExpectedList.size() + 1);
                assertTrue(loginInfoList.contains(newData));
            }
        };

        viewModel.getLoginInfoLiveDataList().observeForever(observer);

        assertTrue(observer.isOnChangedCalled());
    }

    @Test
    public void updateLoginInfoTest() {
        int firstElement = 0;

        LoginInfo updatedData = loginInfoExpectedList.get(firstElement);
        updatedData.setUsername("new_name");
        updatedData.setPassword("new_password");

        viewModel.updateLoginInfo(updatedData);

        observer = new TestObserver() {
            @Override
            public void onChangedBehaviour(List<LoginInfo> loginInfoList) {
                assertNotNull(loginInfoList);
                assertFalse(loginInfoList.isEmpty());
                assertEquals(loginInfoList.size(), loginInfoExpectedList.size());

                LoginInfo loginInfo = loginInfoList.get(firstElement);
                assertEquals(updatedData, loginInfo);
            }
        };

        viewModel.getLoginInfoLiveDataList().observeForever(observer);

        assertTrue(observer.isOnChangedCalled());
    }

    @Test
    public void deleteLoginInfoTest() {
        int firstElement = 0;

        viewModel.deleteLoginInfo(loginInfoExpectedList.get(firstElement));

        observer = new TestObserver() {
            @Override
            public void onChangedBehaviour(List<LoginInfo> loginInfoList) {
                assertNotNull(loginInfoList);
                assertFalse(loginInfoList.isEmpty());
                assertFalse(loginInfoList.contains(loginInfoExpectedList.get(firstElement)));
            }
        };

        viewModel.getLoginInfoLiveDataList().observeForever(observer);

        assertTrue(observer.isOnChangedCalled());
    }

    @Test
    public void deleteAllLoginInfoTest() {
        viewModel.deleteAllLoginInfo();

        observer = new TestObserver() {
            @Override
            public void onChangedBehaviour(List<LoginInfo> loginInfoList) {
                assertNotNull(loginInfoList);
                assertTrue(loginInfoList.isEmpty());
            }
        };

        viewModel.getLoginInfoLiveDataList().observeForever(observer);

        assertTrue(observer.isOnChangedCalled());
    }

}