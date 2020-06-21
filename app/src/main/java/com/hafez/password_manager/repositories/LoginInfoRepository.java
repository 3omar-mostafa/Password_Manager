package com.hafez.password_manager.repositories;

import androidx.lifecycle.MutableLiveData;
import com.hafez.password_manager.models.LoginInfo;
import java.util.List;

public interface LoginInfoRepository {

    void getLoginInfoList(MutableLiveData<List<LoginInfo>> liveData);
}
