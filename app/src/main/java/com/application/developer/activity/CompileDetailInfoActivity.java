package com.application.developer.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.application.developer.R;
import com.application.developer.util.CompileInformationManager;

/**
 * 编译错误详信息
 */
public class CompileDetailInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compile_detail_info);
        TextView textView = findViewById(R.id.tvContent);
        textView.setText(CompileInformationManager.getInstance().getCompileErrorMsg());
        findViewById(R.id.btnClose).setOnClickListener(view -> finish());
    }
}
