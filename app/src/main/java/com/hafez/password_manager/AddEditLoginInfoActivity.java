package com.hafez.password_manager;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.hafez.password_manager.databinding.ActivityAddEditLoginInfoBinding;

public class AddEditLoginInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAddEditLoginInfoBinding viewBinding = ActivityAddEditLoginInfoBinding
                .inflate(getLayoutInflater());

        setContentView(viewBinding.getRoot());

        viewBinding.save.setOnClickListener(v -> {
            //TODO: Actually Save Before Closing
            finish();
        });

    }
}