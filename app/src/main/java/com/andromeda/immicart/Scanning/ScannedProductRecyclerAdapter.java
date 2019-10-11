package com.andromeda.immicart.Scanning;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.andromeda.immicart.R;
import com.bumptech.glide.Glide;


public class ScannedProductRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<ScannedProduct> offerCategories = new ArrayList<>();

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private OnItemClickListener mOnItemClickListener;

    Context context;
    PopupWindow changeCartItemNumberPopUp;

    public interface OnItemClickListener {
        public void onItemClick(String recentSearch);
    }

    public ScannedProductRecyclerAdapter(ArrayList<ScannedProduct> offerCategories, Context context) {
        this.offerCategories = offerCategories;
        this.mOnItemClickListener = mOnItemClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scanned_product, parent, false);
            //inflate your layout and pass it to view holder
            return new VH(view);
        } else if (viewType == TYPE_HEADER) {
            //inflate your layout and pass it to view holder
            View viewHeader = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_see_more, parent, false);

            return new VHHeader(viewHeader);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)

    {

        if (getItemViewType(position) == TYPE_ITEM) {

            if (offerCategories != null) {

                ScannedProductRecyclerAdapter.VH holderItems = (VH) holder;


                ScannedProduct scannedProduct = offerCategories.get(position);
                holderItems.description_product.setText(scannedProduct.getDescription());
                String price = "KES " + scannedProduct.getUnitPrice();
                holderItems.price_tv.setText(price);


                Glide.with(context)
                        .load(scannedProduct.getImageUrl())
                        .into(holderItems.scanned_product_image);

                holderItems.quantity_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int[] location = new int[2];
                        holderItems.quantity_tv.getLocationOnScreen(location);
                        int x = location[0];
                        int y = location[1];
                        Point point = new Point(x, y);
                        showChangeCartItemNumberPopUp(context, point);
                    }
                });
            }

        } else  if (getItemViewType(position) == TYPE_HEADER) {

            if (holder instanceof ScannedProductRecyclerAdapter.VHHeader) {
                ScannedProductRecyclerAdapter.VHHeader headerVH = (ScannedProductRecyclerAdapter.VHHeader) holder;

                headerVH.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        context.startActivity(new Intent(context, OffersActivity.class));
                    }
                });

            }


        }


    }


    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == getItemCount() - 1;
    }

    @Override
    public int getItemCount() {
        return offerCategories.size() + 1;
    }

    public class VH extends RecyclerView.ViewHolder{
        @Nullable
        @BindView(R.id.description_product)
        TextView description_product;
        @Nullable
        @BindView(R.id.remove_item)
        TextView remove_item;
        @Nullable
        @BindView(R.id.quantity_tv)
        TextView quantity_tv;
        @Nullable
        @BindView(R.id.price_tv)
        TextView price_tv;
        @Nullable
        @BindView(R.id.scanned_product_image)
        ImageView scanned_product_image;
        public VH(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public class VHHeader extends RecyclerView.ViewHolder{
        @Nullable
        @BindView(R.id.see_more_ll)
        LinearLayout see_more;
        public VHHeader(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    private void showChangeCartItemNumberPopUp(final Context context, Point p)
    {
        // Inflate the popup_layout.xml
//        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.add_to_cart_pop_up, null);

        // Creating the PopupWindow
        changeCartItemNumberPopUp = new PopupWindow(context);
        changeCartItemNumberPopUp.setContentView(layout);
        changeCartItemNumberPopUp.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeCartItemNumberPopUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeCartItemNumberPopUp.setFocusable(true);
        changeCartItemNumberPopUp.setElevation(20);


        // Some offset to align the popup a bit to the left, and a bit down, relative to button's position.
//        int OFFSET_X = -20;
//        int OFFSET_Y = 95;

        int OFFSET_X = 0;
        int OFFSET_Y = 0;

        // Clear the default translucent background
        changeCartItemNumberPopUp.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        changeCartItemNumberPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);


        // Getting a reference to Close button, and close the popup when clicked.
        ImageButton close = (ImageButton) layout.findViewById(R.id.add_quantity);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeCartItemNumberPopUp.dismiss();
            }
        });

    }


}