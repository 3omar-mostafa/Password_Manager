package com.hafez.password_manager.view_models;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.hafez.password_manager.models.LoginInfo;
import com.hafez.password_manager.repositories.LoginInfoRepository;
import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private LiveData<List<LoginInfo>> loginInfoList;
    private LoginInfoRepository repository;

    public MainActivityViewModel(LoginInfoRepository repository) {
        this.repository = repository;
        this.loginInfoList = repository.getAllLoginInfoList();
    }

    public LiveData<List<LoginInfo>> getLoginInfoLiveDataList() {
        return loginInfoList;
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
