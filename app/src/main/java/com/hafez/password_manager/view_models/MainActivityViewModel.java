package com.hafez.password_manager.view_models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.hafez.password_manager.models.LoginInfo;
import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<List<LoginInfo>> loginInfoList = new MutableLiveData<>();

    public void setLoginInfoList(List<LoginInfo> loginInfoList) {
        this.loginInfoList.setValue(loginInfoList);
    }

    public void addLoginInfo(LoginInfo loginInfo) {
        if (loginInfoList.getValue() == null) {
            loginInfoList.setValue(new ArrayList<>());
        }
        loginInfoList.getValue().add(loginInfo);
    }

    public List<LoginInfo> getLoginInfoList() {
        return loginInfoList.getValue();
    }

}
