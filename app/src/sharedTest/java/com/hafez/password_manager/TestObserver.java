package com.hafez.password_manager;

import androidx.lifecycle.Observer;
import com.hafez.password_manager.models.LoginInfo;
import java.util.List;

public abstract class TestObserver implements Observer<List<LoginInfo>> {

    private boolean isCalled = false;

    public abstract void onChangedBehaviour(List<LoginInfo> loginInfoList);

    @Override
    public void onChanged(List<LoginInfo> loginInfoList) {
        onChangedBehaviour(loginInfoList);
        isCalled = true;
    }

    public boolean isOnChangedCalled() {
        return isCalled;
    }

}
