package com.example.sunshineairlines_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.JsonToken;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin=findViewById(R.id.login_btn_login);
        final EditText inputEmail=findViewById(R.id.login_input_email);
        final EditText inputPassword=findViewById(R.id.login_input_password);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email=inputEmail.getText().toString();
                final String password=inputPassword.getText().toString();
                Future<Boolean> future=Utils.threads.submit(new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        String result=Utils.sendPost(String.format(Utils.baseUrl+"/login?Email=%s&Password=%s",email,password),null);
                        return result!=null&&result.equals("true");
                    }
                });
                boolean result=false;
                try {
                    result=future.get();
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(result){
                    Toast.makeText(LoginActivity.this,"Login succeeded!",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(LoginActivity.this,"Login failed!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
