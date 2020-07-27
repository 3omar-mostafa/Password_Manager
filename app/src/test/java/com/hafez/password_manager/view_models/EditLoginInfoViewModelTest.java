package com.hafez.password_manager.view_models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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

/**
 * The {@link AddEditLoginInfoViewModel} Class Test is separated into two test classes, class for
 * Add part which is in {@link AddLoginInfoViewModelTest} , and class for Edit part which is this
 * class, i.e. {@link EditLoginInfoViewModelTest}.
 */
@RunWith(JUnit4.class)
public class EditLoginInfoViewModelTest {

    // Allows Android Architecture Components (ex. Live Data) to executes each task synchronously.
    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    private AddEditLoginInfoViewModel viewModel;

    private List<LoginInfoFull> loginInfoExpectedList;
    private LoginInfoRepository repository;
    private LoginInfoFull toBeEditedLoginInfo;

    @Before
    public void init() {
        repository = setUpMockRepository();

        toBeEditedLoginInfo = loginInfoExpectedList.get(0);

        viewModel = new AddEditLoginInfoViewModel.Factory(repository, toBeEditedLoginInfo.getId())
                .create(AddEditLoginInfoViewModel.class);
    }

    public LoginInfoRepository setUpMockRepository() {

        loginInfoExpectedList = new ArrayList<>();

        loginInfoExpectedList.add(new LoginInfoFull(1, "user_1", "pass_1", R.drawable.ic_launcher));
        loginInfoExpectedList.add(new LoginInfoFull(2, "user_2", "pass_2", R.drawable.ic_launcher));
        loginInfoExpectedList.add(new LoginInfoFull(3, "user_2", "pass_3", R.drawable.ic_launcher));

        return new MockRepository(loginInfoExpectedList);
    }


    @Test
    public void updateLoginInfoTest() {
        int firstElement = 0;

        LoginInfoFull updatedData = loginInfoExpectedList.get(firstElement);
        updatedData.setUsername("new_name");
        updatedData.setPassword("new_password");

        viewModel.insertOrUpdateLoginInfo(updatedData);

        LiveData<List<LoginInfoFull>> liveData = repository.getAllLoginInfoList();
        List<LoginInfoFull> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertNotNull(loginInfoList);
        assertFalse(loginInfoList.isEmpty());
        assertEquals(loginInfoList.size(), loginInfoExpectedList.size());

        LoginInfoFull loginInfo = loginInfoList.get(firstElement);
        assertEquals(updatedData, loginInfo);
    }

    @Test
    public void deleteLoginInfoTest() {
        int firstElement = 0;

        viewModel.deleteLoginInfo(loginInfoExpectedList.get(firstElement));

        LiveData<List<LoginInfoFull>> liveData = repository.getAllLoginInfoList();
        List<LoginInfoFull> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertNotNull(loginInfoList);
        assertFalse(loginInfoList.isEmpty());
        assertEquals(loginInfoExpectedList.size() - 1, loginInfoList.size());
        assertFalse(loginInfoList.contains(loginInfoExpectedList.get(firstElement)));
    }

    @Test
    public void getLoginInfoTest() {
        LiveData<LoginInfoFull> loginInfoLiveData = viewModel.getLoginInfo();

        assertNotNull(loginInfoLiveData);

        LoginInfoFull loginInfo = LiveDataUtils.getValueOf(loginInfoLiveData);

        assertNotNull(loginInfo);
        assertEquals(loginInfo, toBeEditedLoginInfo);
    }

}