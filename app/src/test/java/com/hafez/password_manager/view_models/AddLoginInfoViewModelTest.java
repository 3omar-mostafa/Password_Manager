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
 * Add part which is this class, i.e. {@link AddLoginInfoViewModelTest} , and class for Edit part
 * which is in {@link EditLoginInfoViewModelTest}.
 */
@RunWith(JUnit4.class)
public class AddLoginInfoViewModelTest {

    // Allows Android Architecture Components (ex. Live Data) to executes each task synchronously.
    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    private AddEditLoginInfoViewModel viewModel;

    private List<LoginInfoFull> loginInfoExpectedList;
    private LoginInfoRepository repository;

    @Before
    public void init() {
        repository = setUpMockRepository();

        viewModel = new AddEditLoginInfoViewModel.Factory(repository)
                .create(AddEditLoginInfoViewModel.class);
    }

    public LoginInfoRepository setUpMockRepository() {

        loginInfoExpectedList = new ArrayList<>();

        loginInfoExpectedList.add(new LoginInfoFull(1, "user_1", "pass_1"));
        loginInfoExpectedList.add(new LoginInfoFull(2, "user_2", "pass_2"));
        loginInfoExpectedList.add(new LoginInfoFull(3, "user_2", "pass_3"));

        return new MockRepository(loginInfoExpectedList);
    }


    @Test
    public void insertLoginInfoTest() {
        LoginInfoFull newData = new LoginInfoFull("new_user", "new_pass");
        newData.setId(loginInfoExpectedList.size() + 1);

        viewModel.insertOrUpdateLoginInfo(newData);

        LiveData<List<LoginInfoFull>> liveData = repository.getAllLoginInfoList();
        List<LoginInfoFull> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertNotNull(loginInfoList);
        assertFalse(loginInfoList.isEmpty());
        assertEquals(loginInfoList.size(), loginInfoExpectedList.size() + 1);
        assertTrue(loginInfoList.contains(newData));
    }

    @Test
    public void deleteUnExistingLoginInfoTest() {
        LoginInfoFull unExistingElement = new LoginInfoFull("does_not_exist", "does_not_exist");
        unExistingElement.setId(loginInfoExpectedList.size() + 1);

        viewModel.deleteLoginInfo(unExistingElement);

        LiveData<List<LoginInfoFull>> liveData = repository.getAllLoginInfoList();
        List<LoginInfoFull> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertNotNull(loginInfoList);
        assertFalse(loginInfoList.isEmpty());
        assertEquals(loginInfoExpectedList.size(), loginInfoList.size());
        assertFalse(loginInfoList.contains(unExistingElement));
    }

    @Test
    public void getLoginInfoTest() {
        LiveData<LoginInfoFull> loginInfo = viewModel.getLoginInfo();
        assertNull(loginInfo);
    }
}