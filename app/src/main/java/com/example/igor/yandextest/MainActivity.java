package com.example.igor.yandextest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity implements OnBandClickCallback,
        LoadDataTask.LoadDataTaskCallback {
    private static String PREFS_URL = "url";
    private static String PREFS_LAST_MOD = "lastMod";
    private static String PREFS_FILE_NAME = "fileName";
    private static String PREFS_NAME = "YandexTest";
    private static String DEFAULT_FILE_NAME = "cache.json";
    private static String FILTER_STRING = "filterString";

    private static String TAG = "MainActivity";

    ArrayList <Band> bands = new ArrayList<>();

    RecyclerView bandsList;
    BandsAdapter bandsAdapter;
    FloatingActionButton fabButton;

    SharedPreferences prefs;

    HashSet<String> setGenres = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        fabButton = (FloatingActionButton) findViewById(R.id.fabButton);
        if (fabButton != null)
            fabButton.setOnClickListener(fabClick);

        bandsList = (RecyclerView) findViewById(R.id.bands_list);

        setTitle(getString(R.string.bands));

        bandsAdapter = new BandsAdapter(bands, this);
        bandsList.setAdapter(bandsAdapter);
        bandsList.setLayoutManager(new LinearLayoutManager(this));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.bands));
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        }

        loadLocalData();
    }

    View.OnClickListener fabClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showGenresDialog(setGenres);
        }
    };

    private void showGenresDialog(Collection<String> genres) {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle(getString(R.string.filter_by_tag));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.select_dialog_multichoice);

        for (String g: genres)
            arrayAdapter.add(g);

        builderSingle.setNegativeButton(
                getString(R.string.clear),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        setSelectedGenre("");
                    }
                });

        builderSingle.setPositiveButton(
                getString(R.string.Ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setSingleChoiceItems(
                arrayAdapter,
                -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        setSelectedGenre(strName);
                    }
                });

        builderSingle.show();
    }

    private void setSelectedGenre(String strName) {
        bandsAdapter.getFilter().filter(strName);
    }

    void loadLocalData(){
        String strUrl = getString(R.string.data_url);
        long lastMod = 0;
        String fileName = "";

        if (prefs.contains(strUrl)){
            if (strUrl.equals(prefs.getString(PREFS_URL, ""))) {
                lastMod = prefs.getLong(PREFS_LAST_MOD, 0);
                fileName = prefs.getString(PREFS_FILE_NAME, "");
            }
        }

        if (lastMod != 0 && !fileName.isEmpty()){
            try {
                String cachedData = Helpers.readFromFile(fileName, this);

                parseData(new JSONArray(cachedData));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to load local data");
            }
        }

        LoadDataTask loadDataTask = new LoadDataTask(this);
        loadDataTask.execute(strUrl);
    }

    @Override
    public void onBandClick(Band band) {
        Intent intent = new Intent(this, BandDetailActivity.class);
        intent.putExtra(BandDetailActivity.BAND_INFO, band);
        startActivity(intent);
    }

    public void loadDataSuccess(Bundle result){
        try {
            checkCachedAndParseData(result);
        } catch (JSONException | IOException e) {
            loadDataError(e);
        }
    }

    public void loadDataError(Exception e){
        e.printStackTrace();
        Toast.makeText(this, R.string.data_load_error, Toast.LENGTH_LONG).show();
    }

    private void checkCachedAndParseData(Bundle result) throws JSONException, IOException {
        long lastModCache = prefs.getLong(PREFS_LAST_MOD, 0);
        long lastModResult = result.getLong(LoadDataTask.LAST_MOD);
        String dataUrl = getResources().getString(R.string.data_url);
        String dataResult = result.getString(LoadDataTask.QUERY_RESULT);

        if (lastModCache == lastModResult
                && prefs.getString(PREFS_URL, "").equals(dataUrl))
        {
            Log.i(TAG, "Cache hit, data is already loaded");
        } else {
            Log.i(TAG, "Cache miss");
            parseData(new JSONArray());
        }

        prefs.edit().clear();
        prefs.edit().putString(PREFS_URL, dataUrl).apply();
        prefs.edit().putLong(PREFS_LAST_MOD, lastModResult).apply();
        prefs.edit().putString(PREFS_FILE_NAME, DEFAULT_FILE_NAME).apply();
        Helpers.writeToFile(dataResult, DEFAULT_FILE_NAME, getApplicationContext());
    }

    private void parseData(JSONArray jsonObject) throws JSONException {
        for (int i = 0; i < jsonObject.length(); i++) {
            JSONObject band = jsonObject.getJSONObject(i);

            bands.add(new Band(band));

            for (String g: bands.get(bands.size()-1).genres){
                setGenres.add(g);
            }
        }

        Collections.sort(bands, new Band.BandComparator());
        bandsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putString(FILTER_STRING, ((BandFilter)bandsAdapter.getFilter()).getFilterStr());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);

        bandsAdapter.getFilter().filter(savedInstanceState.getString(FILTER_STRING));
    }
}
