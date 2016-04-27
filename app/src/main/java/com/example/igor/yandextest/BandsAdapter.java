package com.example.igor.yandextest;

/**
 * Created by igor on 24.04.16.
 */


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BandsAdapter extends RecyclerView.Adapter<BandsAdapter.BandItemViewHolder>
        implements Filterable
{
    ArrayList<Band> bands;
    ArrayList<Band> filteredList = new ArrayList<>();

    private OnBandClickCallback onBandClicksCallback;

    BandsAdapter (ArrayList <Band> bands, OnBandClickCallback callback){
        this.bands = bands;
        this.onBandClicksCallback = callback;
    }

    @Override
    public BandItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.band_item_layout, parent, false);
        return new BandItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BandItemViewHolder holder, int position) {
        holder.band = getBand(position);
        holder.bandName.setText(holder.band.name);

        Glide.with(holder.context)
                .load(holder.band.coverSmall)
                .into(holder.bandAvatar);

        String bandStatsStr = String.format(
                holder.context.getString(R.string.band_item_stats_format),
                holder.band.albums, holder.band.tracks);
        holder.bandStats.setText(bandStatsStr);

        holder.bandGenres.setText(Helpers.strJoin(holder.band.genres, ", "));
    }

    Band getBand(int position){
        if (filteredList.size() > 0)
            return filteredList.get(position);
        else
            return bands.get(position);
    }

    @Override
    public int getItemCount() {
        if (filteredList.size() > 0) return filteredList.size();
        return bands.size();
    }

    @Override
    public Filter getFilter() {
        return new BandFilter(this, bands);
    }

    public class BandItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        Band band;

        Context context;
        private final TextView bandStats;
        private final TextView bandName;
        private final ImageView bandAvatar;
        private final TextView bandGenres;

        public BandItemViewHolder(View itemView) {
            super(itemView);

            context = itemView.getContext();
            itemView.setOnClickListener(this);
            bandName = (TextView) itemView.findViewById(R.id.band_name);
            bandAvatar = (ImageView) itemView.findViewById(R.id.band_avatar);
            bandStats = (TextView) itemView.findViewById(R.id.band_stats);
            bandGenres = (TextView) itemView.findViewById(R.id.band_genres);
        }

        @Override
        public void onClick(View view) {
            onBandClicksCallback.onBandClick(band);
        }
    }
}