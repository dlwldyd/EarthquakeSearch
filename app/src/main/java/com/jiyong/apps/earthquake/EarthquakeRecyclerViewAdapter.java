package com.jiyong.apps.earthquake;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EarthquakeRecyclerViewAdapter extends RecyclerView.Adapter<EarthquakeRecyclerViewAdapter.ViewHolder> {
    private final List<Earthquake> mEarthquakes;
    private static final SimpleDateFormat TIME_FORMAT=new SimpleDateFormat("MM/dd   HH:mm", Locale.US);
    private static final NumberFormat MAGNITUDE_FORMAT=new DecimalFormat("0.0");
    public EarthquakeRecyclerViewAdapter(List<Earthquake> earthquakes){
        mEarthquakes=earthquakes;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView date;
        public final TextView details;
        public final TextView magnitude;
        public Earthquake earthquake;

        public ViewHolder(View view) {
            super(view);
            date=view.findViewById(R.id.date);
            details=view.findViewById(R.id.details);
            magnitude=view.findViewById(R.id.magnitude);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + details.getText() + " '";
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_earthquake, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Earthquake earthquake=mEarthquakes.get(position);
        holder.date.setText(TIME_FORMAT.format(earthquake.getDate()));
        holder.details.setText(earthquake.getDetails());
        holder.magnitude.setText(MAGNITUDE_FORMAT.format(earthquake.getMagnitude()));
    }
    @Override
    public int getItemCount() {
        return mEarthquakes.size();
    }
}
