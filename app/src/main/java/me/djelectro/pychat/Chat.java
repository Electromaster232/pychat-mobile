package me.djelectro.pychat;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Random;

import javax.net.ssl.SSLContext;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import me.djelectro.pychat.utils.CreateHttps;
import okhttp3.OkHttpClient;

public class Chat extends AppCompatActivity {
    Socket socket;
    String isGroup;
    String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_chat);
        CreateHttps https = new CreateHttps();
        setTitle("#" + getIntent().getStringExtra("channel"));

        if(getIntent().getStringExtra("channel").startsWith("g-")){
            isGroup = "yes";
        }
        else{
            isGroup = "no";
        }

        final TextView textViewToChange = (TextView) findViewById(R.id.textView2);
        textViewToChange.setMovementMethod(new ScrollingMovementMethod());
        Toast.makeText(getApplicationContext(), "Talking to server...", Toast.LENGTH_LONG).show();
        // Create socket.io
        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .hostnameVerifier(https.createHostnameVerifier())
                    .sslSocketFactory(SSLContext.getDefault().getSocketFactory(), https.createX509TrustManager())
                    .build();

            IO.setDefaultOkHttpWebSocketFactory(okHttpClient);
            IO.setDefaultOkHttpCallFactory(okHttpClient);

            IO.Options opts = new IO.Options();
            opts.callFactory = okHttpClient;
            opts.webSocketFactory = okHttpClient;
            socket = IO.socket("https://chat.djelectro.me");
            socket.connect();
            JSONObject obj = new JSONObject();
            System.out.println(getIntent().getStringExtra("username"));
            obj.put("user_name", getIntent().getStringExtra("username").replace("\n", ""));
            obj.put("token", getIntent().getStringExtra("token"));
            System.out.println(getIntent().getStringExtra("token"));
            obj.put("channel", getIntent().getStringExtra("channel"));
            Random rand = new Random();
            key = String.valueOf(rand.nextInt(998) + 1);
            obj.put("key", key);
            obj.put("referrer", "/chat/"+getIntent().getStringExtra("channel"));
            socket.emit("joinree", obj);

            JSONObject getPrev = new JSONObject();
            getPrev.put("channel", getIntent().getStringExtra("channel"));
            getPrev.put("key", key);
            getPrev.put("group", isGroup);
            socket.emit("getprevmsg", getPrev);



        } catch (Exception e){
            throw new RuntimeException(e);
        }
        socket.on("chatrecieve", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {

                        JSONObject obj = (JSONObject) args[0];
                        String message = obj.getString("user_name").replace("<i class='fa fa-gavel'></i>", "\uD83D\uDD28") + " > " + obj.getString("message").replace("<p>", "").replace("</p>", "");

                        textViewToChange.append("\n" + message);

                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }
                }
            });


        socket.on("recvprevmsg", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {

                    JSONObject obj = (JSONObject) args[0];
                    if(obj.getString("key").equals(key)){
                        String message = obj.getString("user_name").replace("<i class='fa fa-gavel'></i>", "\uD83D\uDD28") + " > " + obj.getString("message").replace("<p>", "").replace("</p>", "");

                        textViewToChange.append("\n" + message);
                    }


                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        });
    }


    public void sendMessage(View view) {
        try {
            EditText editText = (EditText) findViewById(R.id.editText);
            String message = editText.getText().toString();
            JSONObject obj = new JSONObject();
            obj.put("user_name", getIntent().getStringExtra("username").replace("\n", ""));
            obj.put("token", getIntent().getStringExtra("token"));
            obj.put("message", message);
            obj.put("channel", getIntent().getStringExtra("channel"));
            obj.put("group", isGroup);
            socket.emit("chatsend", obj);
            editText.setText("");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
