package com.hafez.password_manager;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.hafez.password_manager.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding viewBinding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(viewBinding.getRoot());
    }

}