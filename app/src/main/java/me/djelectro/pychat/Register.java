package me.djelectro.pychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import me.djelectro.pychat.utils.DisplayToast;
import me.djelectro.pychat.utils.Request;

public class Register extends AppCompatActivity {
    Request webRequest;
    DisplayToast displayToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webRequest = new Request();
        displayToast = new DisplayToast();
        setContentView(R.layout.activity_register);
    }

    public void sendRegister(View view){
        EditText editText = (EditText) findViewById(R.id.usernameRegister);
        EditText editText1 = (EditText) findViewById(R.id.passwordRegister);
        EditText editText2 = (EditText) findViewById(R.id.emailRegister);
        String username = editText.getText().toString();
        String password = editText1.getText().toString();
        String email = editText2.getText().toString();
        try {
            String res = webRequest.execute("https://chat.djelectro.me/signup", "username=" + username.replace("\n", "") + "&password=" + password.replace("\n", "") + "&email=" + email.replace("\n", "")).get();
        }catch (Exception e){
            e.printStackTrace();
        }
        displayToast.displayToast(getApplicationContext(), "Successfully registered! Sending you to the login...");
        Intent intent2 = new Intent(this, Login.class);
        startActivity(intent2);

    }

}
