package kr.publicm.mask;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap gMap;
    private Map<String, Marker> markerList = new HashMap<>();

    private DataLint dataLint;

    public MapFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataLint = new DataLint(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = view.findViewById(R.id.mapView);
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMyLocationEnabled(true);
    }

    public void setFocusOn(CustomLocation location) {
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLat(), location.getLng()), 16));
    }

    public void setFocusOn(CustomLocation location, String name) {
        gMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLat(), location.getLng())));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        markerList.get(name).showInfoWindow();
    }

    public void setMarkerList(MaskInfo maskInfo) {
        MaskInfo.Store[] stores = maskInfo.getStores();
        if (gMap == null) {
            return;
        }

        if (markerList.isEmpty()) {
            CustomLocation refLocation = maskInfo.getRefLocation();
            setFocusOn(refLocation);
        }

        gMap.clear();
        markerList.clear();

        for (MaskInfo.Store store : stores) {
            DataLint.Stock stock = dataLint.getStockText(store.remain_stat);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(store.name);
            markerOptions.position(new LatLng(store.lat, store.lng));
            markerOptions.snippet(String.format("%s | %s", dataLint.getTimeOffset(store.stock_at), stock.getStockText()));
            if (store.remain_stat.equals("break")) {
                markerOptions.alpha(0.15f);
            } else if (store.remain_stat.equals("empty")) {
                markerOptions.alpha(0.35f);
            } else {
                markerOptions.alpha(0.9f);
            }
            float[] hsv = new float[3];
            Color.colorToHSV(getResources().getColor(stock.getColor(), null), hsv);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(hsv[0]));
            markerList.put(store.name, gMap.addMarker(markerOptions));
        }
    }
}
