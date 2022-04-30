package com.example.sunshineairlines_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.JsonToken;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
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

        if(Utils.debug){
            inputEmail.setText("tt@gmail.com");
            inputPassword.setText("123456");
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();
                Future<Boolean> future = Utils.threads.submit(new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        String result = Utils.sendPost(String.format(Utils.baseUrl + "/login?Email=%s&Password=%s", email, password), null);
                        return result != null && result.equals("true");
                    }
                });
                boolean result = false;
                try {
                    result = future.get();
                    if (result) {
                        inputPassword.setText("");
                        UserInfo info = Utils.threads.submit(new Callable<UserInfo>() {
                            @Override
                            public UserInfo call() {
                                try {
                                    String infoStr = Utils.sendGet(Utils.baseUrl + "/user/" + email, null);
                                    JSONObject infoObj = new JSONArray(infoStr).getJSONObject(0);
                                    UserInfo info = new UserInfo();
                                    info.id = infoObj.getString("ID");
                                    info.firstName = infoObj.getString("FirstName");
                                    info.lastName = infoObj.getString("LastName");
                                    info.male = infoObj.getString("Gender").equals("M");
                                    return info;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }
                        }).get();
                        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                        intent.putExtra("userInfo", info);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
