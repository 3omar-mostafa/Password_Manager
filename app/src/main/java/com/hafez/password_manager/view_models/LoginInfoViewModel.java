package com.hafez.password_manager.view_models;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.hafez.password_manager.models.LoginInfo;
import com.hafez.password_manager.repositories.DatabaseRepository;
import com.hafez.password_manager.repositories.LoginInfoRepository;
import java.util.List;

public class LoginInfoViewModel extends ViewModel {

    private LiveData<List<LoginInfo>> loginInfoList;
    private LoginInfoRepository repository;

    public LoginInfoViewModel(@NonNull LoginInfoRepository repository) {
        this.repository = repository;
        this.loginInfoList = repository.getAllLoginInfoList();
    }

    public LiveData<List<LoginInfo>> getLoginInfoLiveDataList() {
        return loginInfoList;
    }

    public void deleteLoginInfo(LoginInfo loginInfo) {
        repository.delete(loginInfo);
    }


    public void insert(LoginInfo loginInfo) {
        repository.insert(loginInfo);
    }

    public void deleteAllLoginInfo() {
        repository.deleteAllLoginInfo();
    }


    public static class Factory implements ViewModelProvider.Factory {

        private LoginInfoRepository repository;

        public Factory(LoginInfoRepository repository) {
            this.repository = repository;
        }

        public Factory() {
            this(new DatabaseRepository());
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new LoginInfoViewModel(repository);
        }
    }

}
