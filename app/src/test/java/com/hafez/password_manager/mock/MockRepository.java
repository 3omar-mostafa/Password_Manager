package com.hafez.password_manager.mock;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.hafez.password_manager.models.LoginInfo;
import com.hafez.password_manager.repositories.LoginInfoRepository;
import java.util.ArrayList;
import java.util.List;

public class MockRepository implements LoginInfoRepository {

    private MutableLiveData<List<LoginInfo>> database;

    public MockRepository(@NonNull List<LoginInfo> loginInfoList) {
        database = new MutableLiveData<>(new ArrayList<>(loginInfoList));
    }

    @Override
    public void insert(LoginInfo loginInfo) {
        List<LoginInfo> loginInfoList = database.getValue();
        loginInfoList.add(loginInfo);
        database.setValue(new ArrayList<>(loginInfoList));
    }

    @Override
    public void update(LoginInfo loginInfo) {
        List<LoginInfo> loginInfoList = database.getValue();

        for (int i = 0; i < loginInfoList.size(); i++) {
            if (loginInfoList.get(i).getId() == loginInfo.getId()) {
                loginInfoList.remove(i);
                loginInfoList.add(i, loginInfo);
                break;
            }
        }

        database.setValue(new ArrayList<>(loginInfoList));
    }

    @Override
    public void delete(LoginInfo loginInfo) {
        List<LoginInfo> loginInfoList = database.getValue();
        loginInfoList.remove(loginInfo);
        database.setValue(new ArrayList<>(loginInfoList));
    }

    @Override
    public void deleteAllLoginInfo() {
        database.setValue(new ArrayList<>());
    }

    @Override
    public LiveData<List<LoginInfo>> getAllLoginInfoList() {
        return database;
    }
}