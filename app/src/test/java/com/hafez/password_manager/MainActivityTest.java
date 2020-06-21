package com.hafez.password_manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
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
    private TestObserver observer;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        setUpMockRepository();
        viewModel = new MainActivityViewModel.Factory(repository)
                .create(MainActivityViewModel.class);
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

    @After
    public void cleanup() {
        viewModel.getLoginInfoLiveDataList().removeObserver(observer);
    }


    @Test
    public void getLoginInfoLiveDataListTest() {

        observer = new TestObserver() {
            @Override
            public void onChangedBehaviour(List<LoginInfo> loginInfoList) {
                assertEquals(loginInfoList, loginInfoExpectedList);
            }
        };

        viewModel.getLoginInfoLiveDataList().observeForever(observer);

        assertTrue(observer.isOnChangedCalled());

    }

    private abstract static class TestObserver implements Observer<List<LoginInfo>> {

        private boolean isCalled = false;

        public abstract void onChangedBehaviour(List<LoginInfo> loginInfoList);

        @Override
        public void onChanged(List<LoginInfo> loginInfoList) {
            onChangedBehaviour(loginInfoList);
            isCalled = true;
        }

        boolean isOnChangedCalled() {
            return isCalled;
        }

    }

}