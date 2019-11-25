package com.andromeda.immicart.delivery;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.andromeda.immicart.R;

public class PlacesAutoCompleteAdapter extends RecyclerView.Adapter<PlacesAutoCompleteAdapter.PlacesViewHolder>{

    private ArrayList<Place> placeArrayList;
    private Context context;
    private OnItemClickListener onItemClickListener;


    public PlacesAutoCompleteAdapter(ArrayList<Place> places, Context context,
                                     OnItemClickListener onItemClickListener) {
        this.placeArrayList = places;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void OnItemClick(String recommended);

    }


    @NonNull
    @Override
    public PlacesAutoCompleteAdapter.PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        int layout = R.layout.item_store_picker;
        PlacesAutoCompleteAdapter.PlacesViewHolder viewHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(layout, parent, false);
        viewHolder = new PlacesAutoCompleteAdapter.PlacesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesAutoCompleteAdapter.PlacesViewHolder holder, int position) {
        if(placeArrayList != null){
            Place place = placeArrayList.get(position);
            holder.place_name.setText(place.getName());
            holder.placeAddress.setText(place.getAddress());

        }

    }

    @Override
    public int getItemCount() {
        if(placeArrayList != null){
            return placeArrayList.size();
        }
        else{
            return 0;
        }
    }



    class PlacesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name)
        TextView place_name;

        @BindView(R.id.address)
        TextView placeAddress;


        PlacesViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


    }
}
