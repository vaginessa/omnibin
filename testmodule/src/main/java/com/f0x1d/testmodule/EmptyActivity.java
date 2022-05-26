package com.f0x1d.testmodule;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getData() != null) {
            Intent intent = new Intent("com.f0x1d.dogbin.ACTION_TEXT_VIEW");
            intent.setType("text/plain");
            intent.putExtra("module_package_name", getPackageName());
            intent.putExtra("url", getIntent().getData().toString());

            startActivity(intent);
            finishAndRemoveTask();
        }
    }
}
