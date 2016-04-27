package com.example.igor.yandextest;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by igor on 24.04.16.
 */
public class Band implements Parcelable {
    long id;
    String name;
    ArrayList <String> genres = new ArrayList<>();

    int tracks;
    int albums;
    String link;
    String biography;

    String coverSmall;
    String coverBig;

    Band (JSONObject bandInfo){
        this.id = bandInfo.optInt("id");
        this.name = bandInfo.optString("name");
        this.tracks = bandInfo.optInt("tracks");
        this.albums = bandInfo.optInt("albums");
        this.link = bandInfo.optString("link");
        this.biography = bandInfo.optString("description");

        try {
            JSONObject cover = bandInfo.getJSONObject("cover");
            this.coverSmall = cover.getString("small");
            this.coverBig = cover.getString("big");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray genresJSON = bandInfo.getJSONArray("genres");

            for (int i = 0; i < genresJSON.length(); i++) {
                this.genres.add(genresJSON.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected Band(Parcel in) {
        id = in.readLong();
        name = in.readString();
        genres = in.createStringArrayList();
        tracks = in.readInt();
        albums = in.readInt();
        link = in.readString();
        biography = in.readString();
        coverSmall = in.readString();
        coverBig = in.readString();
    }

    public static final Creator<Band> CREATOR = new Creator<Band>() {
        @Override
        public Band createFromParcel(Parcel in) {
            return new Band(in);
        }

        @Override
        public Band[] newArray(int size) {
            return new Band[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeStringList(genres);
        dest.writeInt(tracks);
        dest.writeInt(albums);
        dest.writeString(link);
        dest.writeString(biography);
        dest.writeString(coverSmall);
        dest.writeString(coverBig);
    }

    public static class BandComparator implements Comparator<Band> {
        @Override
        public int compare(Band o1, Band o2) {
            return o1.name.compareTo(o2.name);
        }
    }
}


