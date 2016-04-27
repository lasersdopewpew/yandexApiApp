package com.example.igor.yandextest;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by igor on 24.04.16.
 */
public class Helpers {
    public static String strJoin(ArrayList<String> genres, String delimiter) {
        if (genres == null || genres.size() == 0) return "";

        StringBuilder result = new StringBuilder();

        Iterator<String> it = genres.iterator();
        while(it.hasNext())        {
            result.append(it.next());
            if (it.hasNext()) result.append(delimiter);
        }

        return result.toString();
    }

    public static void writeToFile(String data, String fileName, Context context) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                context.openFileOutput(fileName, Context.MODE_PRIVATE));
        outputStreamWriter.write(data);
        outputStreamWriter.close();
    }

    public static String readFromFile(String filename, Context context) throws IOException {

        String ret = "";

        InputStream inputStream = context.openFileInput(filename);

        if ( inputStream != null ) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String receiveString;
            StringBuilder stringBuilder = new StringBuilder();

            while ( (receiveString = bufferedReader.readLine()) != null ) {
                stringBuilder.append(receiveString);
            }

            inputStream.close();
            ret = stringBuilder.toString();
        }

        return ret;
    }
}
