package me.djelectro.pychat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.FileOutputStream;

import me.djelectro.pychat.utils.Request;

public class Login extends AppCompatActivity {
    Request webRequest;
    String res;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        webRequest = new Request();
        if(getIntent().getStringExtra("username") == null){
        }
        else {
            executeLogin(getIntent().getStringExtra("username"), getIntent().getStringExtra("password"));
        }
    }

    public void doLogin(View view){
        EditText editText = (EditText) findViewById(R.id.username);
        EditText editText1 = (EditText) findViewById(R.id.password);
        String username = editText.getText().toString();
        String password = editText1.getText().toString();
        executeLogin(username, password);
    }

    private void executeLogin(String username, String password){
        try {
            res = webRequest.execute("https://chat.djelectro.me/getcookie", "username=" + username.replace("\n", "") + "&password=" + password.replace("\n", "")).get();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        String filename = "password";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(password.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String filename2 = "username";
        FileOutputStream outputStream2;

        try {
            outputStream2 = openFileOutput(filename2, Context.MODE_PRIVATE);
            outputStream2.write(username.getBytes());
            outputStream2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, ChannelSelect.class);
        intent.putExtra("username", username);
        intent.putExtra("token", res);
        startActivity(intent);
    }
}
