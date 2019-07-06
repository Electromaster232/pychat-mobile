package me.djelectro.pychat.utils;

import android.content.Context;
import android.widget.Toast;

public class DisplayToast {

    public void displayToast(Context appContext, String text){
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(appContext, text, duration).show();
    }

}
