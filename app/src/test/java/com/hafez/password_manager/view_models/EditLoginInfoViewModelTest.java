package com.hafez.password_manager.view_models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
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

/**
 * The {@link AddEditLoginInfoViewModel} Class Test is separated into two test classes, class for
 * Add part which is in {@link AddLoginInfoViewModelTest} , and class for Edit part which is this
 * class, i.e. {@link EditLoginInfoViewModelTest}.
 */
@RunWith(JUnit4.class)
public class EditLoginInfoViewModelTest {

    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    private AddEditLoginInfoViewModel viewModel;

    private List<LoginInfo> loginInfoExpectedList;
    private TestObserver<List<LoginInfo>> allListObserver;
    private LoginInfoRepository repository;
    private LoginInfo toBeEditedLoginInfo;
    private TestObserver<LoginInfo> toBeEditedLoginInfoObserver;

    @Before
    public void init() {
        repository = setUpMockRepository();

        toBeEditedLoginInfo = loginInfoExpectedList.get(0);

        viewModel = new AddEditLoginInfoViewModel.Factory(repository, toBeEditedLoginInfo.getId())
                .create(AddEditLoginInfoViewModel.class);
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
        repository.getAllLoginInfoList().removeObserver(allListObserver);
        if (toBeEditedLoginInfoObserver != null) {
            viewModel.getLoginInfo().removeObserver(toBeEditedLoginInfoObserver);
        }
    }


    @Test
    public void updateLoginInfoTest() {
        int firstElement = 0;

        LoginInfo updatedData = loginInfoExpectedList.get(firstElement);
        updatedData.setUsername("new_name");
        updatedData.setPassword("new_password");

        viewModel.insertOrUpdateLoginInfo(updatedData);

        allListObserver = new TestObserver<List<LoginInfo>>() {
            @Override
            public void onChangedBehaviour(List<LoginInfo> loginInfoList) {
                assertNotNull(loginInfoList);
                assertFalse(loginInfoList.isEmpty());
                assertEquals(loginInfoList.size(), loginInfoExpectedList.size());

                LoginInfo loginInfo = loginInfoList.get(firstElement);
                assertEquals(updatedData, loginInfo);
            }
        };

        repository.getAllLoginInfoList().observeForever(allListObserver);

        assertTrue(allListObserver.isOnChangedCalled());
    }

    @Test
    public void deleteLoginInfoTest() {
        int firstElement = 0;

        viewModel.deleteLoginInfo(loginInfoExpectedList.get(firstElement));

        allListObserver = new TestObserver<List<LoginInfo>>() {
            @Override
            public void onChangedBehaviour(List<LoginInfo> loginInfoList) {
                assertNotNull(loginInfoList);
                assertFalse(loginInfoList.isEmpty());
                assertEquals(loginInfoExpectedList.size() - 1, loginInfoList.size());
                assertFalse(loginInfoList.contains(loginInfoExpectedList.get(firstElement)));
            }
        };

        repository.getAllLoginInfoList().observeForever(allListObserver);

        assertTrue(allListObserver.isOnChangedCalled());
    }

    @Test
    public void getLoginInfoTest() {
        LiveData<LoginInfo> loginInfoLiveData = viewModel.getLoginInfo();

        assertNotNull(loginInfoLiveData);

        toBeEditedLoginInfoObserver = new TestObserver<LoginInfo>() {
            @Override
            public void onChangedBehaviour(LoginInfo loginInfo) {
                assertNotNull(loginInfo);
                assertEquals(loginInfo, toBeEditedLoginInfo);
            }
        };

        loginInfoLiveData.observeForever(toBeEditedLoginInfoObserver);

        assertTrue(toBeEditedLoginInfoObserver.isOnChangedCalled());
    }

}