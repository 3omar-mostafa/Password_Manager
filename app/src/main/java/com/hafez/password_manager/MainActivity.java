package com.hafez.password_manager;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.hafez.password_manager.databinding.ActivityMainBinding;
import com.hafez.password_manager.view_models.LoginInfoViewModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding viewBinding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(viewBinding.getRoot());

        LoginInfoViewModel viewModel = new ViewModelProvider(this,
                new LoginInfoViewModel.Factory()).get(LoginInfoViewModel.class);

        LoginInfoAdapter adapter = new LoginInfoAdapter();
        viewBinding.loginInfoRecyclerView.setAdapter(adapter);
        viewBinding.loginInfoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getLoginInfoLiveDataList()
                .observe(this, loginInfoList -> adapter.submitList(loginInfoList));

        adapter.setOnItemClickListener((view, loginInfo) -> {
            Intent intent = new Intent(MainActivity.this, AddEditLoginInfoActivity.class);
            intent.putExtra(AddEditLoginInfoActivity.ARGUMENT_LOGIN_INFO_ID, loginInfo.getId());
            startActivity(intent);
        });

        viewBinding.add.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditLoginInfoActivity.class);
            startActivity(intent);
        });

    }

}