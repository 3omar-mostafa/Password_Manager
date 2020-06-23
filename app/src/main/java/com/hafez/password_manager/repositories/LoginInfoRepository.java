package com.hafez.password_manager.repositories;

import androidx.lifecycle.LiveData;
import com.hafez.password_manager.models.LoginInfo;
import java.util.List;

public interface LoginInfoRepository {

    void insert(LoginInfo loginInfo);

    void update(LoginInfo loginInfo);

    void delete(LoginInfo loginInfo);

    void deleteAllLoginInfo();

    LiveData<List<LoginInfo>> getAllLoginInfoList();

    LiveData<LoginInfo> getLoginInfo(long id);
}
