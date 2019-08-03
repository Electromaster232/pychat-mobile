package me.djelectro.pychat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import me.djelectro.pychat.utils.DisplayToast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String fileAsString = "";
        String fileAsString2 = "";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context appContext = getApplicationContext();
        File directory = appContext.getFilesDir();
        try {
            File file = new File(directory, "password");
            InputStream is = new FileInputStream(file);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));

            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();

            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }

            fileAsString = sb.toString();


            File file2 = new File(directory, "username");
            InputStream is2 = new FileInputStream(file2);
            BufferedReader buf2 = new BufferedReader(new InputStreamReader(is2));

            String line2 = buf2.readLine();
            StringBuilder sb2 = new StringBuilder();

            while (line2 != null) {
                sb2.append(line2).append("\n");
                line2 = buf2.readLine();
            }

            fileAsString2 = sb2.toString();

        }
        catch (IOException e) {
            e.printStackTrace();
        }


        if(fileAsString != "" && fileAsString2 != ""){
            System.out.println(fileAsString);
            System.out.println(fileAsString2);
            Intent intent2 = new Intent(this, Login.class);
            intent2.putExtra("username", fileAsString2);
            intent2.putExtra("password", fileAsString);
            startActivity(intent2);
        }
    }

    public void toLogin(View view){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);

    }


    public void toRegister(View view){
        Intent intent2 = new Intent(this, Register.class);
        startActivity(intent2);
    }
}
