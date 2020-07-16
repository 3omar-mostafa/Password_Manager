package com.hafez.password_manager.view_models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import com.hafez.password_manager.LiveDataUtils;
import com.hafez.password_manager.R;
import com.hafez.password_manager.mock.MockRepository;
import com.hafez.password_manager.models.LoginInfo;
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
 * Add part which is this class, i.e. {@link AddLoginInfoViewModelTest} , and class for Edit part
 * which is in {@link EditLoginInfoViewModelTest}.
 */
@RunWith(JUnit4.class)
public class AddLoginInfoViewModelTest {

    // Allows Android Architecture Components (ex. Live Data) to executes each task synchronously.
    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    private AddEditLoginInfoViewModel viewModel;

    private List<LoginInfo> loginInfoExpectedList;
    private LoginInfoRepository repository;

    @Before
    public void init() {
        repository = setUpMockRepository();

        viewModel = new AddEditLoginInfoViewModel.Factory(repository)
                .create(AddEditLoginInfoViewModel.class);
    }

    public LoginInfoRepository setUpMockRepository() {

        loginInfoExpectedList = new ArrayList<>();

        loginInfoExpectedList.add(new LoginInfo(1, "user_1", "pass_1", R.drawable.ic_launcher));
        loginInfoExpectedList.add(new LoginInfo(2, "user_2", "pass_2", R.drawable.ic_launcher));
        loginInfoExpectedList.add(new LoginInfo(3, "user_2", "pass_3", R.drawable.ic_launcher));

        return new MockRepository(loginInfoExpectedList);
    }


    @Test
    public void insertLoginInfoTest() {
        LoginInfo newData = new LoginInfo("new_user", "new_pass", 0);
        newData.setId(loginInfoExpectedList.size() + 1);

        viewModel.insertOrUpdateLoginInfo(newData);

        LiveData<List<LoginInfo>> liveData = repository.getAllLoginInfoList();
        List<LoginInfo> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertNotNull(loginInfoList);
        assertFalse(loginInfoList.isEmpty());
        assertEquals(loginInfoList.size(), loginInfoExpectedList.size() + 1);
        assertTrue(loginInfoList.contains(newData));
    }

    @Test
    public void deleteUnExistingLoginInfoTest() {
        LoginInfo unExistingElement = new LoginInfo("does_not_exist", "does_not_exist", 0);
        unExistingElement.setId(loginInfoExpectedList.size() + 1);

        viewModel.deleteLoginInfo(unExistingElement);

        LiveData<List<LoginInfo>> liveData = repository.getAllLoginInfoList();
        List<LoginInfo> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertNotNull(loginInfoList);
        assertFalse(loginInfoList.isEmpty());
        assertEquals(loginInfoExpectedList.size(), loginInfoList.size());
        assertFalse(loginInfoList.contains(unExistingElement));
    }

    @Test
    public void getLoginInfoTest() {
        LiveData<LoginInfo> loginInfo = viewModel.getLoginInfo();
        assertNull(loginInfo);
    }
}