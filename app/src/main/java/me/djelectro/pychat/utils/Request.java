package me.djelectro.pychat.utils;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

import javax.net.ssl.HttpsURLConnection;

public class Request extends AsyncTask<String, String, String>{

    public String response;
    private final String USER_AGENT = "PyChat/5.0";

    // HTTP POST request
    @Override
    protected String doInBackground(String... args){
        try {
            String url = args[0];
            String urlParameters = args[1];
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");


            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //return result
            System.out.println(response.toString());
            return response.toString();
        } catch (IOException e){
            e.printStackTrace();
            return "";
        }

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        response = result;

    }

}

