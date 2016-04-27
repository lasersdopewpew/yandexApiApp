package com.example.igor.yandextest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class BandDetailActivity extends AppCompatActivity {
    public static final String BAND_INFO = "BAND_INFO";

    ImageView ivBandImage;
    TextView tvBandGenres;
    TextView tvBandStats;
    TextView tvBandBiography;
    Toolbar tbToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_detail);

        ivBandImage = (ImageView) findViewById(R.id.band_image);
        tvBandGenres = (TextView) findViewById(R.id.band_genres);
        tvBandStats = (TextView) findViewById(R.id.band_stats);
        tvBandBiography = (TextView) findViewById(R.id.band_biography);
        tbToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (tbToolbar != null) {
            tbToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_36dp);
            tbToolbar.setTitleTextColor(getResources().getColor(R.color.white));

            tbToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        Intent intent = getIntent();
        Band band = intent.getExtras().getParcelable(BAND_INFO);
        parseData(band);
    }

    private void parseData(Band band) {
        tvBandGenres.setText(Helpers.strJoin(band.genres, ", "));
        tvBandBiography.setText(band.biography);

        String bandStatsStr = String.format(getString(R.string.fomat_stats_band_detail),
                band.albums, band.tracks);
        tvBandStats.setText(bandStatsStr);

        Glide.with(this).load(band.coverBig).into(ivBandImage);

        tbToolbar.setTitle(band.name);
    }
}
