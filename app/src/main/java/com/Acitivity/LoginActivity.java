package com.Acitivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wecanws.param.R;

/**
 * 登录界面 2017/6/28
 */
public class LoginActivity extends Activity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.btn_login).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
           case  R.id.btn_login:
               Intent intent=new Intent(this,LoginActivity.class);
               startActivity(intent);
            break;
        }
    }
}


