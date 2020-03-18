package com.andromeda.immicart.delivery.choose_store;


import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.andromeda.immicart.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    ArrayList<Store> stores = new ArrayList<>();
    private Context context;

    private OnItemClickListener mOnItemClickListener;

    LatLng currentLocation;
    private static final String TAG = "StoreAdapter";


    public interface OnItemClickListener {
        public void onItemClick(Store store);
    }


    public StoreAdapter(ArrayList<Store> stores, Context context, OnItemClickListener onItemClickListener) {
        this.stores = stores;
        this.context = context;
        this.mOnItemClickListener = onItemClickListener;
//        this.currentLocation = currentLocation;
    }

    @NonNull
    @Override
    public StoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreAdapter.ViewHolder holder, int position) {

        if (stores != null) {
            Log.d(TAG, "Stores NOT NULL");

            Store store = stores.get(position);
            Log.d(TAG, "Stores : " + store);
            holder.store.setText(store.getName());
//            String timeOpen = " " + store.get() + " ";
//            holder.time_open.setText(timeOpen);

//            String timeClosed = " " + store.getTimeClosed() + " ";
//            holder.time_closed.setText(timeClosed);

            Glide.with(context)
                    .load(store.getLogoUrl())
                    .into(holder.store_image);


//            String reviews = store.getNumber_of_reviews() + " reviews";
//            holder.reviews.setText(reviews);

            holder.location.setText(store.getAddress());
            String storeLatLng = store.getLatlng();
            LatLng _storeLatLng = computeLatLng(storeLatLng);

//            double distance = computeDistance(currentLocation, _storeLatLng);
//            Log.d(TAG, "Second call: calculateDistance");
//            calculateDistance(currentLocation, _storeLatLng);
            LatLng latLng = new LatLng(-1.289261, 36.82416);
            Log.d(TAG, "Distance from KICC");
            String distance_ = calculateDistance(latLng, _storeLatLng);


            holder.distance.setText(distance_);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(store);
                }
            });

        }

    }

    String processDistance(int distance) {
        if (distance > 1000) {
            float distanceComputed = ((float) distance)/1000;
            double doubleDis = round(distanceComputed, 1);
            return doubleDis + "km away";

        } else if (distance >100) {
            double distanceComputed = ((double) distance)/1000;
            Log.d(TAG, "double computed: " + distanceComputed);
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.CEILING);

            return df.format(distanceComputed) + "km away";

        } else {
            return distance + "m away";

        }
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    private String calculateDistance(LatLng oldPosition, LatLng newPosition) {
        // The computed distance is stored in results[0].
        //If results has length 2 or greater, the initial bearing is stored in results[1].
        //If results has length 3 or greater, the final bearing is stored in results[2].
        float[] results = new float[1];
        Location.distanceBetween(oldPosition.latitude, oldPosition.longitude,
                newPosition.latitude, newPosition.longitude, results);
        float result = results[0];
        int disatnce = ((int) result);

        Log.d(TAG, "Distance : " + disatnce);
        String distanceComputed = processDistance(disatnce);
        Log.d(TAG, "processDistance : " + distanceComputed);
        return distanceComputed;

    }

    double computeDistance(LatLng latLngFrom, LatLng latLngTo) {
        return  SphericalUtil.computeDistanceBetween(latLngFrom, latLngTo);

    }

    LatLng computeLatLng(String example) {
        String from_lat_lng = null;
        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(example);
        while(m.find()) {
            from_lat_lng = m.group(1) ;
        }
        String[] gpsVal = from_lat_lng.split(",");
        double lat = Double.parseDouble(gpsVal[0]);
        double lon = Double.parseDouble(gpsVal[1]);
        Log.d(TAG, "LatLng computeLatLng: " + new LatLng(lat, lon));

        return  new LatLng(lat, lon);
    }

    @Override
    public int getItemCount() {
        if (stores != null) {
            return stores.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.store_image)
        ImageView store_image = itemView.findViewById(R.id.store_image);
//        @BindView(R.id.store)
        TextView store = itemView.findViewById(R.id.store);
//        @BindView(R.id.location)
        TextView location = itemView.findViewById(R.id.location);
//        @BindView(R.id.time_open)
        TextView time_open = itemView.findViewById(R.id.time_open);
//        @BindView(R.id.distance)
        TextView distance = itemView.findViewById(R.id.distance);

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            ButterKnife.bind(this, itemView);
        }
    }
}

