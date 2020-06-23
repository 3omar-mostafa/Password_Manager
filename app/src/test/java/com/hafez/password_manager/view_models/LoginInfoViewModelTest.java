package com.hafez.password_manager.view_models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.hafez.password_manager.R;
import com.hafez.password_manager.TestObserver;
import com.hafez.password_manager.mock.MockRepository;
import com.hafez.password_manager.models.LoginInfo;
import com.hafez.password_manager.repositories.LoginInfoRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LoginInfoViewModelTest {

    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    private LoginInfoViewModel viewModel;

    private List<LoginInfo> loginInfoExpectedList;
    private TestObserver<List<LoginInfo>> observer;

    @Before
    public void init() {
        LoginInfoRepository repository = setUpMockRepository();

        viewModel = new LoginInfoViewModel.Factory(repository).create(LoginInfoViewModel.class);
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

        observer = new TestObserver<List<LoginInfo>>() {
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
    public void deleteLoginInfoTest() {
        int firstElement = 0;

        viewModel.deleteLoginInfo(loginInfoExpectedList.get(firstElement));

        observer = new TestObserver<List<LoginInfo>>() {
            @Override
            public void onChangedBehaviour(List<LoginInfo> loginInfoList) {
                assertNotNull(loginInfoList);
                assertFalse(loginInfoList.isEmpty());
                assertEquals(loginInfoExpectedList.size() - 1, loginInfoList.size());
                assertFalse(loginInfoList.contains(loginInfoExpectedList.get(firstElement)));
            }
        };

        viewModel.getLoginInfoLiveDataList().observeForever(observer);

        assertTrue(observer.isOnChangedCalled());
    }

    @Test
    public void deleteAllLoginInfoTest() {
        viewModel.deleteAllLoginInfo();

        observer = new TestObserver<List<LoginInfo>>() {
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