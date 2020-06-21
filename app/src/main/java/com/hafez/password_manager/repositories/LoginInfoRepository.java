package com.hafez.password_manager.repositories;

import androidx.lifecycle.LiveData;
import com.hafez.password_manager.models.LoginInfo;
import java.util.List;

public interface LoginInfoRepository {

    LiveData<List<LoginInfo>> getAllLoginInfoList();
}
