package com.hafez.password_manager;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.hafez.password_manager.databinding.ActivityMainBinding;
import com.hafez.password_manager.view_models.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding viewBinding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(viewBinding.getRoot());

        MainActivityViewModel viewModel = new ViewModelProvider(this,
                new MainActivityViewModel.Factory()).get(MainActivityViewModel.class);

        LoginInfoAdapter adapter = new LoginInfoAdapter();
        viewBinding.loginInfoRecyclerView.setAdapter(adapter);
        viewBinding.loginInfoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getLoginInfoLiveDataList()
                .observe(this, loginInfoList -> adapter.submitList(loginInfoList));

    }

}