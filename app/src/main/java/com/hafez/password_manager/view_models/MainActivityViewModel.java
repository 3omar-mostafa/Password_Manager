package com.hafez.password_manager.view_models;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.hafez.password_manager.models.LoginInfo;
import com.hafez.password_manager.repositories.LoginInfoRepository;
import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<List<LoginInfo>> loginInfoList = new MutableLiveData<>();
    private LoginInfoRepository repository;

    public MainActivityViewModel(LoginInfoRepository repository) {
        this.repository = repository;
    }

    public void setLoginInfoList(List<LoginInfo> loginInfoList) {
        this.loginInfoList.setValue(loginInfoList);
    }

    public void addLoginInfo(LoginInfo loginInfo) {
        if (loginInfoList.getValue() == null) {
            loginInfoList.setValue(new ArrayList<>());
        }
        loginInfoList.getValue().add(loginInfo);
    }

    public LiveData<List<LoginInfo>> getLoginInfoLiveDataList() {
        return loginInfoList;
    }

    public void requestLoginInfoList() {
        repository.getLoginInfoList(loginInfoList);
    }


    public static class Factory implements ViewModelProvider.Factory {

        private LoginInfoRepository repository;


        public Factory(LoginInfoRepository repository) {
            this.repository = repository;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new MainActivityViewModel(repository);
        }
    }

}
