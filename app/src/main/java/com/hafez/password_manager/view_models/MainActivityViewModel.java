package com.hafez.password_manager.view_models;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.hafez.password_manager.models.LoginInfo;
import com.hafez.password_manager.repositories.LoginInfoRepository;
import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private LiveData<List<LoginInfo>> loginInfoList = new MutableLiveData<>();
    private LoginInfoRepository repository;

    public MainActivityViewModel(LoginInfoRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<LoginInfo>> getLoginInfoLiveDataList() {
        return loginInfoList;
    }

    public void requestLoginInfoList() {
        loginInfoList = repository.getAllLoginInfoList();
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
