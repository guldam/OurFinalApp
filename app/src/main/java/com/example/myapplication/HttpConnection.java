package com.example.myapplication;

import android.os.AsyncTask;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by q on 2017-01-03.
 */

public class HttpConnection extends AsyncTask<JSONArray, Void, Void> {
    public Void doInBackground(JSONArray... params) {


        try {

            String url = "http://52.78.19.20:8080";
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            JSONArray jsonArray = params[0];
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jsonArray.toString().getBytes("UTF-8"));
            outputStream.flush();

            String inputLine;
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }
            in.close();
            outputStream.close();
            conn.disconnect();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

}
