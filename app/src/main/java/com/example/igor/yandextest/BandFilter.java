package com.example.igor.yandextest;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by igor on 25.04.16.
 */
public class BandFilter extends Filter {

    private final BandsAdapter adapter;

    private final List<Band> originalList;
    private final List<Band> filteredList;
    private String filterStr = "";

    public String getFilterStr() {
        return filterStr;
    }

    public BandFilter(BandsAdapter adapter, List<Band> originalList) {
        super();
        this.adapter = adapter;
        this.originalList = new LinkedList<>(originalList);
        this.filteredList = new ArrayList<>();
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        filteredList.clear();
        final FilterResults results = new FilterResults();

        if (constraint.length() == 0) {
            filteredList.clear();
        } else {
            final String filterPattern = constraint.toString();
            filterStr = filterPattern;

            for (final Band user : originalList) {
                if (user.genres.contains(filterPattern)) {
                    filteredList.add(user);
                }
            }
        }
        results.values = filteredList;
        results.count = filteredList.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.filteredList.clear();
        adapter.filteredList.addAll((ArrayList<Band>) results.values);
        adapter.notifyDataSetChanged();
    }
}