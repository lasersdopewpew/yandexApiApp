package com.example.igor.yandextest;

import android.os.AsyncTask;
import android.os.Bundle;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by igor on 25.04.16.
 */

public class LoadDataTask extends AsyncTask<String, Void, AsyncTaskResult> {
    public static String LAST_MOD = "lastMod";
    public static String QUERY_RESULT = "queryResult";

    LoadDataTaskCallback callback;

    LoadDataTask(LoadDataTaskCallback callback){
        this.callback = callback;
    }

    @Override
    protected AsyncTaskResult doInBackground(String... params) {
        try {
            if (params.length < 0) {
                throw new RuntimeException("Wrong param count");
            }

            String strUrl = params[0];

            URL url;
            url = new URL(strUrl);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();
            long lastMod = urlConnection.getLastModified();

            if (statusCode == HttpURLConnection.HTTP_OK) {
                InputStream it = new BufferedInputStream(urlConnection.getInputStream());
                InputStreamReader read = new InputStreamReader(it);
                BufferedReader buff = new BufferedReader(read);
                StringBuilder data = new StringBuilder();
                String chunks;
                while((chunks = buff.readLine()) != null) {
                    data.append(chunks);
                }

                Bundle result = new Bundle();
                result.putString(QUERY_RESULT, data.toString());
                result.putLong(LAST_MOD, lastMod);

                return new AsyncTaskResult<>(result);
            } else {
                return new AsyncTaskResult(
                        new RuntimeException("Connect error" + statusCode));
            }
        } catch (Exception e) {
            return new AsyncTaskResult<>(e);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult result) {
        super.onPostExecute(result);

        if (result.getResult() != null) callback.loadDataSuccess((Bundle) result.getResult());
        else if (result.getError() != null) callback.loadDataError(result.getError());
    }

    interface LoadDataTaskCallback{
        void loadDataSuccess(Bundle result);
        void loadDataError(Exception e);
    }
}