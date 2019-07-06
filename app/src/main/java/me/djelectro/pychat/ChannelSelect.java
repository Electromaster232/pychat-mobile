package me.djelectro.pychat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.File;

public class ChannelSelect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_select);
        if(getIntent().getStringExtra("username") == null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }


    public void doLogout(View view){
        Context appContext = getApplicationContext();
        File directory = appContext.getFilesDir();
        File file = new File(directory, "password");
        File file2 = new File(directory, "username");
        file.delete();
        file2.delete();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void startChat(View view){
        EditText editText = (EditText) findViewById(R.id.editText2);
        String channel = editText.getText().toString().replace(" ", "");
        Intent intent = new Intent(this, Chat.class);
        intent.putExtra("username", getIntent().getStringExtra("username"));
        intent.putExtra("token", getIntent().getStringExtra("token"));
        intent.putExtra("channel", channel);
        startActivity(intent);
    }
}
