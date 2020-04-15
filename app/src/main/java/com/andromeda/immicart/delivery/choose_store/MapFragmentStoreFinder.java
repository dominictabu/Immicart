package com.andromeda.immicart.delivery.choose_store;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.andromeda.immicart.delivery.ProductsPageActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.airbnb.android.airmapview.AirMapMarker;
import com.airbnb.android.airmapview.AirMapView;
import com.airbnb.android.airmapview.listeners.OnMapInitializedListener;
import com.airbnb.android.airmapview.listeners.OnMapMarkerClickListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.andromeda.immicart.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragmentStoreFinder#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragmentStoreFinder extends Fragment implements OnMapInitializedListener, StoreAdapter.OnItemClickListener, OnMapMarkerClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "MapFragmentStoreFinder";
    private static final String REQUEST_START_SCANNING = "REQUEST_START_SCANNING";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    private  MapFragmentStoreFinderViewModel mapFragmentStoreFinderViewModel;


    @BindView(R.id.recycler_view_stores)
    RecyclerView mRecyclerView;
    @BindView(R.id.map)
    AirMapView map;
    @BindView(R.id.close_button)
    ImageView close_button;
    private int locationRequestCode = 1000;
    private boolean mLocationPermissionGranted;
    Location mLastKnownLocation;
    private static final String REQUEST_START_STORE = "REQUEST_START_STORE";

    ArrayList<Store> stores_ = new ArrayList<>();


    private FirebaseFirestore db;

    private FusedLocationProviderClient mFusedLocationClient;

    ArrayList<AirMapMarker> airMapMarkers = new ArrayList<>();

    public MapFragmentStoreFinder() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter one.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragmentStoreFinder.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragmentStoreFinder newInstance(String param1, String param2) {
        MapFragmentStoreFinder fragment = new MapFragmentStoreFinder();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static MapFragmentStoreFinder newInstance() {
        MapFragmentStoreFinder fragment = new MapFragmentStoreFinder();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
//        mapFragmentStoreFinderViewModel = ViewModelProviders.of(this).get(MapFragmentStoreFinderViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_map_fragment_store_finder, container, false);
        ButterKnife.bind(this, view);
        map.initialize(getChildFragmentManager());
        map.setOnMapInitializedListener(this);
        map.setOnMarkerClickListener(this);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated Called");

        db = FirebaseFirestore.getInstance();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));

        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

//        Store store = new Store(0,"Tuskys Supermarket", R.drawable.furniture,4, 438, "Moi Avenue", "7:30AM", "8:00PM");
//        Store store1 = new Store(one,"Tuskys Supermarket",R.drawable.bakeries, 4, 438, "Moi Avenue", "7:30AM", "8:00PM");
//        Store store2 = new Store(2,"Naivas Supermarket", R.drawable.chair,4, 438, "Two Rivers", "7:30AM", "8:00PM");
//        Store store3 = new Store(3,"Tuskys Supermarket", R.drawable.furniture,5, 438, "JCM", "7:30AM", "8:00PM");
//        Store store4 = new Store(4,"Tuskys Supermarket", R.drawable.chair,4, 438, "Moi Avenue", "7:30AM", "8:00PM");
//        Store store5 = new Store(5,"Tuskys Supermarket", R.drawable.bakeries,3, 438, "Moi Avenue", "7:30AM", "8:00PM");
//        Store store6 = new Store(6,"Tuskys Supermarket", R.drawable.bakeries,3, 438, "Moi Avenue", "7:30AM", "8:00PM");
//        Store store7 = new Store(7,"Tuskys Supermarket", R.drawable.bakeries,3, 438, "Moi Avenue", "7:30AM", "8:00PM");
//        Store store8 = new Store(8,"Tuskys Supermarket", R.drawable.bakeries,3, 438, "Moi Avenue", "7:30AM", "8:00PM");
//
//        stores.add(store);
//        stores.add(store1);
//        stores.add(store2);
//        stores.add(store3);
//        stores.add(store4);
//        stores.add(store5);
//        stores.add(store6);
//        stores.add(store7);
//        stores.add(store4);
//        stores.add(store8);
//
//        StoreAdapter storeAdapter = new StoreAdapter(stores, getActivity(), this);
//
//        mRecyclerView.setAdapter(storeAdapter);


//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                int position = linearLayoutManager.findFirstVisibleItemPosition();
//
//                if(airMapMarkers.size() != 0 && position < airMapMarkers.size()) {
//
//                    AirMapMarker airMapMarker = airMapMarkers.get(position);
//                    Marker marker = airMapMarker.getMarker();
//                    marker.showInfoWindow();
//                }
//
//            }
//        });

        // map.setMapType(MapType.MAP_TYPE_NORMAL);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;

                    getDeviceLocation();
                } else {
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Log.d(TAG, "mLocationPermissionGranted true in getDeviceLocation");

                Task locationResult = mFusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "task.isSuccessful() true in getDeviceLocation");

                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            String location = null;
                            if (mLastKnownLocation != null) {
                                Log.d(TAG, "mLastKnownLocation not NULL in getDeviceLocation");

                                location = mLastKnownLocation.toString();

//                                addMarker("Currrent Location", new LatLng(mLastKnownLocation.getLatitude(),
//                                        mLastKnownLocation.getLongitude()), one);
//                                addMarker("Performance Bikes", new LatLng(37.773975, -122.40205), 2);//        addMarker("Performance Bikes", new LatLng(37.773975, -122.40205), 2);
//                                addMarker("Performance Bikes", new LatLng(37.773975, -122.40205), 3);
//                                addMarker("Performance Bikes", new LatLng(38.773975, -122.40205), 4);
//                                addMarker("Performance Bikes", new LatLng(39.773975, -122.40205), 5);
//                                addMarker("Performance Bikes", new LatLng(39.273975, -122.40205), 6);
//                                addMarker("Performance Bikes", new LatLng(38.173975, -122.40205), 7);
//                                val new_Pos = LatLng(-1.269295, 36.8086194)

                                LatLng latLng = new LatLng(-1.269295, 36.8086194);
//                               LatLng latLng1 = LatLng() latLng.toString();

//                                map.animateCenterZoom( new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), 12);
                                map.animateCenterZoom( latLng, 14);
                            }

                        } else {
//                            Log.d(TAG, "Current location is null. Using defaults.");
//                            Log.e(TAG, "Exception: %s", task.getException());
//                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
//                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        Bitmap _bmp = Bitmap.createScaledBitmap(output, 100, 100, false);
        return _bmp;
//        return output;
    }

    private void addMarker(Store store, LatLng latLng, int id,
                           String divIconHtml, int iconWidth, int iconHeight) {

        Glide.with(this)
                .asBitmap()
                .load(store.getLogoUrl())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        AirMapMarker airMapMarker =  new AirMapMarker.Builder()
                                .id(id)
                                .position(latLng)
                                .title(store.getName())
                                .bitmapDescriptor(BitmapDescriptorFactory.fromBitmap(getCroppedBitmap(resource)))
//                .divIconHtml(divIconHtml)
//                .divIconWidth(iconWidth)
//                .divIconHeight(iconHeight)
                                .build();
                        airMapMarkers.add(airMapMarker);

                        map.addMarker(airMapMarker);                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
//        AirMapMarker airMapMarker =  new AirMapMarker.Builder()
//                .id(id)
//                .position(latLng)
//                .title(title)
//                .bitmapDescriptor(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_place_green_24dp))
////                .divIconHtml(divIconHtml)
////                .divIconWidth(iconWidth)
////                .divIconHeight(iconHeight)
//                .build();
//        airMapMarkers.add(airMapMarker);
//
//        map.addMarker(airMapMarker);
    }

    private void addMarker(Store store, LatLng latLng, int id) {
        addMarker(store, latLng, id, null, 0, 0);
    }

    @Override
    public void onMapInitialized() {



        Log.d(TAG, "onMApInitialized Called");

//        try {
//            // Customise the styling of the base map using a JSON object defined
//            // in a raw resource file.
//
//            boolean success = map.setMapStyle(
//                    MapStyleOptions.loadRawResourceStyle(
//                            requireActivity(), R.raw.uber_map_style));
//
//            if (!success) {
//                Log.e(TAG, "Style parsing failed.");
//            }
//        } catch (Resources.NotFoundException e) {
//            Log.e(TAG, "Can't find style. Error: ", e);
//        }

        // check permission
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // reuqest for permission
            mLocationPermissionGranted = false;

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);

        } else {
            Log.d(TAG, "onMApInitialized Called: Location Permisiin");
            mLocationPermissionGranted = true;

            if (isLocationEnabled(getActivity())) {
                // already permission granted
                map.setMyLocationEnabled(true);
                getDeviceLocation();
                getStores();

            } else {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Turn on Location in your phone")
                        .setMessage("We need to know your cuuurent location to show you stores around you.")
                        .setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        }).setNegativeButton("Dismiss", null)
                        .show();

            }

        }

//        final LatLng airbnbLatLng = new LatLng(37.771883, -122.405224);
//        addMarker("Airbnb HQ", airbnbLatLng, one);
//        addMarker("Performance Bikes", new LatLng(37.773975, -122.40205), 2);
//        addMarker("REI", new LatLng(37.772127, -122.404411), 3);
//        addMarker("Mapbox", new LatLng(37.77572, -122.41354), 4);
//        map.animateCenterZoom(airbnbLatLng, 10);

    }

    public void finishAndLaunchStore() {
//        Intent intent = new Intent(getActivity(), BottomNavActivity.class);
//        intent.setAction(REQUEST_START_STORE);
//        startActivity(intent);
    }

    private void appendLog(String msg, int position) {
        Log.d(TAG, msg);
        mRecyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void onMapMarkerClick(AirMapMarker<?> airMarker) {
        appendLog("Map onMapMarkerClick triggered with id " + airMarker.getId(), (int) airMarker.getId());

    }

    String CURRENT_STORE = "CURRENT_STORE";
    @Override
    public void onItemClick(Store  store) {
//        mapFragmentStoreFinderViewModel.deleteAll();
//        mapFragmentStoreFinderViewModel.insert(store);
//        setCurrentStore(store);
//        if (mParam1 != null) {
////            startActivity(new Intent(getActivity(), BarcodeScannerActivity.class));
////            Intent intent = new Intent(getActivity(), BottomNavActivity.class);
////            intent.setAction(REQUEST_START_SCANNING);
////            startActivity(intent);
//        } else {
//            finishAndLaunchStore();
//
//        }



        Intent intent = new Intent(requireActivity(), ProductsPageActivity.class);
        intent.putExtra(CURRENT_STORE, store);
        startActivity(intent);
    }


    //Checks whether location services  are enabled
    public static boolean isLocationEnabled(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This is new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // This is Deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return  (mode != Settings.Secure.LOCATION_MODE_OFF);

        } else {
            String locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }


    public void getStores() {
        // [START get_multiple_all]
        Log.d(TAG, "getStores Called");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);

        ArrayList<Store> stores = new ArrayList<>();

        String collectionPath = "stores";
        db.collection(collectionPath)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(TAG, "OnSuccessListener");
                        Log.d(TAG , "QuerySnapshot Size: " + queryDocumentSnapshots.size());
                        Log.d(TAG, "QuerySnapshot " + queryDocumentSnapshots.toString());
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Map<String, Object> map = document.getData();
                            String name = (String) map.get("name");
                            String logoUrl = (String) map.get("logoUrl");
                            String county = (String) map.get("county");
                            String address = (String) map.get("address");
                            String latLng = (String) map.get("latLng");
                            Long number_of_reviews = (Long) map.get("number_of_reviews");
                            Integer i = number_of_reviews != null ? number_of_reviews.intValue() : null;

                            Double avg_rating = (Double) map.get("avg_rating");
                            float _averageRating = (float) avg_rating.floatValue();

                            Store store = new Store(document.getId(),name, logoUrl, county, address, latLng, i, _averageRating, true);

                            stores.add(store);

                        }
//                        if (mLastKnownLocation != null) {
//                            LatLng currentLocation = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            StoreAdapter storeAdapter = new StoreAdapter(stores, getActivity(),MapFragmentStoreFinder.this);
                            addMapMarkers(stores);
                            mRecyclerView.setAdapter(storeAdapter);
//                        }

//                        stores_ = stores;

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Exception: " + e);

            }
        });
        // [END get_multiple_all]
    }

    void addMapMarkers(ArrayList<Store> _store) {
        int i = 0;
        for (Store store : _store) {
            String from_lat_lng = null;
            String latLng = store.getLatlng();
            String example = "lat/lng:(35.000000,119.0000000)";
            Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(latLng);
            while(m.find()) {
                from_lat_lng = m.group(1) ;
            }
            String[] gpsVal = from_lat_lng.split(",");
            double lat = Double.parseDouble(gpsVal[0]);
            double lon = Double.parseDouble(gpsVal[1]);
            addMarker(store, new LatLng(lat, lon), i);
            i++;

        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void setCurrentStore(Store store) {
        HashMap storeMap = new HashMap<String, Object>();
        storeMap.put("storeID", store.getKey());
        storeMap.put("storeName", store.getName());
        storeMap.put("storeLogo", store.getLogoUrl());
        storeMap.put("storeLatLng", store.getLatlng());
        storeMap.put("storeAddress", store.getAddress());


        String userUID = FirebaseAuth.getInstance().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("customers/" + userUID).child("current_store");
        ref.setValue(storeMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Store selected successfully");
                startActivity(new Intent(getActivity(), ProductsPageActivity.class));
            }
        });
    }

}