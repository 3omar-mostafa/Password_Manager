package com.hafez.password_manager.view_models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import com.hafez.password_manager.LiveDataUtils;
import com.hafez.password_manager.R;
import com.hafez.password_manager.mock.MockRepository;
import com.hafez.password_manager.models.LoginInfoFull;
import com.hafez.password_manager.repositories.LoginInfoRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LoginInfoViewModelTest {

    // Allows Android Architecture Components (ex. Live Data) to executes each task synchronously.
    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    private LoginInfoViewModel viewModel;

    private List<LoginInfoFull> loginInfoExpectedList;

    @Before
    public void init() {
        LoginInfoRepository repository = setUpMockRepository();

        viewModel = new LoginInfoViewModel.Factory(repository).create(LoginInfoViewModel.class);
    }

    public LoginInfoRepository setUpMockRepository() {

        loginInfoExpectedList = new ArrayList<>();

        loginInfoExpectedList.add(new LoginInfoFull(1, "user_1", "pass_1", R.drawable.ic_launcher));
        loginInfoExpectedList.add(new LoginInfoFull(2, "user_2", "pass_2", R.drawable.ic_launcher));
        loginInfoExpectedList.add(new LoginInfoFull(3, "user_2", "pass_3", R.drawable.ic_launcher));

        return new MockRepository(loginInfoExpectedList);
    }


    @Test
    public void insertLoginInfoTest() {
        LoginInfoFull newData = new LoginInfoFull("new_user", "new_pass");
        newData.setId(loginInfoExpectedList.size() + 1);

        viewModel.insertLoginInfo(newData);

        LiveData<List<LoginInfoFull>> liveData = viewModel.getLoginInfoLiveDataList();
        List<LoginInfoFull> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertNotNull(loginInfoList);
        assertFalse(loginInfoList.isEmpty());
        assertEquals(loginInfoList.size(), loginInfoExpectedList.size() + 1);
        assertTrue(loginInfoList.contains(newData));
    }

    @Test
    public void getLoginInfoLiveDataListTest() {

        LiveData<List<LoginInfoFull>> liveData = viewModel.getLoginInfoLiveDataList();
        List<LoginInfoFull> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertNotNull(loginInfoList);
        assertFalse(loginInfoList.isEmpty());
        assertEquals(loginInfoList, loginInfoExpectedList);

    }

    @Test
    public void deleteLoginInfoTest() {
        int firstElement = 0;

        viewModel.deleteLoginInfo(loginInfoExpectedList.get(firstElement));

        LiveData<List<LoginInfoFull>> liveData = viewModel.getLoginInfoLiveDataList();
        List<LoginInfoFull> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertNotNull(loginInfoList);
        assertFalse(loginInfoList.isEmpty());
        assertEquals(loginInfoExpectedList.size() - 1, loginInfoList.size());
        assertFalse(loginInfoList.contains(loginInfoExpectedList.get(firstElement)));
    }

    @Test
    public void deleteAllLoginInfoTest() {
        viewModel.deleteAllLoginInfo();

        LiveData<List<LoginInfoFull>> liveData = viewModel.getLoginInfoLiveDataList();
        List<LoginInfoFull> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertNotNull(loginInfoList);
        assertTrue(loginInfoList.isEmpty());
    }

}