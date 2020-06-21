package com.hafez.password_manager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.hafez.password_manager.models.LoginInfo;
import com.hafez.password_manager.repositories.LoginInfoRepository;
import com.hafez.password_manager.view_models.MainActivityViewModel;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MainActivityTest {

    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    @Mock
    private LoginInfoRepository repository;

    @Mock
    private MainActivityViewModel viewModel;

    private List<LoginInfo> loginInfoExpectedList;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        viewModel = new MainActivityViewModel(repository);
    }

    public void setUpMockRepository() {

        loginInfoExpectedList = new ArrayList<>();

        loginInfoExpectedList.add(new LoginInfo("user_1", "pass_1", R.drawable.ic_launcher));
        loginInfoExpectedList.add(new LoginInfo("user_2", "pass_2", R.drawable.ic_launcher));
        loginInfoExpectedList.add(new LoginInfo("user_2", "pass_3", R.drawable.ic_launcher));

        MutableLiveData<List<LoginInfo>> liveData = new MutableLiveData<>();
        liveData.setValue(loginInfoExpectedList);

        when(repository.getAllLoginInfoList()).thenReturn(liveData);
    }

    @Test
    public void viewModelRepositoryTest() {

        setUpMockRepository();

        Observer<List<LoginInfo>> observer = new Observer<List<LoginInfo>>() {
            @Override
            public void onChanged(List<LoginInfo> loginInfoList) {
                assertEquals(loginInfoList, loginInfoExpectedList);
            }
        };

        viewModel.getLoginInfoLiveDataList().observeForever(observer);

        viewModel.requestLoginInfoList();

        viewModel.getLoginInfoLiveDataList().removeObserver(observer);
    }
}