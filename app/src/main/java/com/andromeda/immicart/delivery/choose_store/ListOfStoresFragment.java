package com.andromeda.immicart.delivery.choose_store;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.andromeda.immicart.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListOfStoresFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListOfStoresFragment extends Fragment implements StoreAdapter.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db;

    private FusedLocationProviderClient mFusedLocationClient;

    ListOfStoresViewModel listOfStoresFragmentViewModel;


    @BindView(R.id.recycler_store)
    RecyclerView list_store_recycler;
    @BindView(R.id.loading_view)
    AVLoadingIndicatorView loading_view;

    private static final String TAG = "ListOfStoresFragment";

    public ListOfStoresFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListOfStoresFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListOfStoresFragment newInstance() {
        ListOfStoresFragment fragment = new ListOfStoresFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        listOfStoresFragmentViewModel = ViewModelProviders.of(this).get(ListOfStoresViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_of_stores, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here

            //getAllDocs(location);
            getStores(location);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));
        db = FirebaseFirestore.getInstance();
//        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setPersistenceEnabled(false)
//                .build();
//        db.setFirestoreSettings(settings);
        LocationManager mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        } else {

        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                1000f, mLocationListener);


    }

    public void getAllDocs(Location location) {
        // [START get_multiple_all]

        loading_view.show();
        ArrayList<Store> stores = new ArrayList<>();

        String collectionPath = "stores";
        db.collection(collectionPath).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                loading_view.hide();

                Log.d(TAG, "Stores size" + queryDocumentSnapshots.size());

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

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
                list_store_recycler.setLayoutManager(linearLayoutManager);

                StoreAdapter storeAdapter = new StoreAdapter(stores, getActivity(), latLng,ListOfStoresFragment.this);

                list_store_recycler.setAdapter(storeAdapter);
            }
        });
//        db.collection(collectionPath)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            loading_view.hide();
//
//                            Log.d(TAG, "task.isSuccessful()");
//
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                Map<String, Object> map = document.getData();
//                                String name = (String) map.get("name");
//                                String logoUrl = (String) map.get("logoUrl");
//                                String county = (String) map.get("county");
//                                String address = (String) map.get("address");
//                                String latLng = (String) map.get("latLng");
//                                Long number_of_reviews = (Long) map.get("number_of_reviews");
//                                Integer i = number_of_reviews != null ? number_of_reviews.intValue() : null;
//
//                                Double avg_rating = (Double) map.get("avg_rating");
//                                float _averageRating = (float) avg_rating.floatValue();
//
//
//                                    Store store = new Store(document.getId(),name, logoUrl, county, address, latLng, i, _averageRating, true);
//
//                                    stores.add(store);
//
//
//
//
//                            }
//
//                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//
//                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
//                            list_store_recycler.setLayoutManager(linearLayoutManager);
//
//                            StoreAdapter storeAdapter = new StoreAdapter(stores, getActivity(), latLng,ListOfStoresFragment.this);
//
//                            list_store_recycler.setAdapter(storeAdapter);
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
        // [END get_multiple_all]
    }


    void getStores(Location location) {
        String collectionPath = "stores";
        loading_view.show();
        ArrayList<Store> stores = new ArrayList<>();
        db.collection(collectionPath).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "Stores size" + queryDocumentSnapshots.size());
                loading_view.hide();

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

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
                list_store_recycler.setLayoutManager(linearLayoutManager);

                StoreAdapter storeAdapter = new StoreAdapter(stores, getActivity(), latLng,ListOfStoresFragment.this);

                list_store_recycler.setAdapter(storeAdapter);
            }
        });
    }

    @Override
    public void onItemClick(Store store) {
        listOfStoresFragmentViewModel.deleteAll();
        listOfStoresFragmentViewModel.insert(store);
    }
}