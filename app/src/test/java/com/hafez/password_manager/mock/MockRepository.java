package com.hafez.password_manager.mock;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.hafez.password_manager.models.Category;
import com.hafez.password_manager.models.LoginInfoFull;
import com.hafez.password_manager.repositories.LoginInfoRepository;
import java.util.ArrayList;
import java.util.List;

public class MockRepository implements LoginInfoRepository {

    private MutableLiveData<List<LoginInfoFull>> database;

    public MockRepository(@NonNull List<LoginInfoFull> loginInfoList) {
        database = new MutableLiveData<>(new ArrayList<>(loginInfoList));
    }

    @Override
    public void insert(LoginInfoFull loginInfo) {
        List<LoginInfoFull> loginInfoList = database.getValue();
        loginInfoList.add(loginInfo);
        database.setValue(new ArrayList<>(loginInfoList));
    }

    @Override
    public void update(LoginInfoFull loginInfo) {
        List<LoginInfoFull> loginInfoList = database.getValue();

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
    public void delete(LoginInfoFull loginInfo) {
        List<LoginInfoFull> loginInfoList = database.getValue();
        loginInfoList.remove(loginInfo);
        database.setValue(new ArrayList<>(loginInfoList));
    }

    @Override
    public void deleteAllLoginInfo() {
        database.setValue(new ArrayList<>());
    }

    @Override
    public LiveData<List<LoginInfoFull>> getAllLoginInfoList() {
        return database;
    }

    @Override
    public LiveData<LoginInfoFull> getLoginInfo(long id) {
        MutableLiveData<LoginInfoFull> loginInfoLiveData = new MutableLiveData<>();

        for (LoginInfoFull loginInfo : database.getValue()) {
            if (loginInfo.getId() == id) {
                loginInfoLiveData.setValue(loginInfo);
                break;
            }
        }
        return loginInfoLiveData;
    }

    @Override
    public void insertCategory(Category category) {

    }

    @Override
    public void updateCategory(Category category) {

    }

    @Override
    public void deleteCategory(Category category) {

    }
}
