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

public class PlacesAutoCompleteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<Place> placeArrayList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private static final int FOOTER_VIEW = 1;


    public PlacesAutoCompleteAdapter(ArrayList<Place> places, Context context,
                                     OnItemClickListener onItemClickListener) {
        this.placeArrayList = places;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void OnItemClick(Place place);

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == FOOTER_VIEW) {
            view = LayoutInflater.from(context).inflate(R.layout.item_footer_view, parent, false);
            FooterViewHolder vh = new FooterViewHolder(view);
            return vh;
        }  else {

            int layout = R.layout.item_store_picker;
            PlacesAutoCompleteAdapter.PlacesViewHolder viewHolder;
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(layout, parent, false);
            viewHolder = new PlacesAutoCompleteAdapter.PlacesViewHolder(view);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof PlacesViewHolder) {
                PlacesViewHolder vh = (PlacesViewHolder) holder;

                if(placeArrayList != null){
                    Place place = placeArrayList.get(position);
                    vh.place_name.setText(place.getName());
                    vh.placeAddress.setText(place.getAddress());

                    vh.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Do whatever you want on clicking the item
                            onItemClickListener.OnItemClick(place);
                        }
                    });
                }
            } else if (holder instanceof FooterViewHolder) {
                FooterViewHolder vh = (FooterViewHolder) holder;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        if(placeArrayList != null){

            return placeArrayList.size() + 1;

        } else if (placeArrayList.size() == 0) {
            //Return 1 here to show nothing
            return 1;
        }

        else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == placeArrayList.size()) {
            // This is where we'll add footer.
            return FOOTER_VIEW;
        }

        return super.getItemViewType(position);
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

    // Define a ViewHolder for Footer view
    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);

        }
    }
}
