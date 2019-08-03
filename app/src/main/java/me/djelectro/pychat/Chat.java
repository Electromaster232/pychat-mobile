package me.djelectro.pychat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.net.ssl.SSLContext;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import me.djelectro.pychat.utils.CreateHttps;
import me.djelectro.pychat.utils.Request;
import okhttp3.OkHttpClient;

public class Chat extends AppCompatActivity {
    Socket socket;
    String isGroup;
    String key;
    TextView textViewToChange;
    Integer permsGranted = 0;
    Request webRequest;
    private int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_chat);
        webRequest = new Request();
        CreateHttps https = new CreateHttps();
        setTitle("#" + getIntent().getStringExtra("channel"));

        if(getIntent().getStringExtra("channel").startsWith("g-")){
            isGroup = "yes";
        }
        else{
            isGroup = "no";
        }
        textViewToChange = (TextView) findViewById(R.id.textView2);
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
            getPrev.put("mobile", "yes");
            socket.emit("getprevmsg", getPrev);



        } catch (JSONException e){
            e.printStackTrace();
        }
        catch (URISyntaxException e){
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        socket.on("chatrecieve", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject obj = (JSONObject) args[0];
                        String message = obj.getString("user_name").replace("<i class='fa fa-gavel'></i>", "\uD83D\uDD28") + " > " + obj.getString("message").replace("<p>", "").replace("</p>", "");

                        appendText(message);

                    }catch (JSONException e){
                        e.printStackTrace();
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
                        //System.out.println(message);
                        appendText(message);
                    }


                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

        socket.on("imageurl", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject) args[0];
                try {
                    EditText editText = (EditText) findViewById(R.id.editText);
                    editText.setText(editText.getText() + obj.getString("url"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });



        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //do what you want on the press of 'done'
                    sendMessage();
                }
                return true;
            }
        });
    }

    public void appendText(String newText){
        TextView textViewToChange = (TextView) findViewById(R.id.textView2);
        String curr = textViewToChange.getText().toString();
        String message = curr + "\n" + newText;
        textViewToChange.setText(message);
    }


    public void sendMessage() {
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
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void uploadFile(View view){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    permsGranted);

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return;

        }
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, PICK_IMAGE_REQUEST);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, PICK_IMAGE_REQUEST);
                }
    }


    private String encodeImage(Uri uri)
    {
        try {
            InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(uri);
            Bitmap bm = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String encImage = Base64.encodeToString(b, Base64.DEFAULT);
            //Base64.de
            return encImage;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            String encImage = encodeImage(uri);
            socket.emit("image", encImage);
        }
    }



}
