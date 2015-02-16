package com.yscn.knucommunity.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ApplicationUtil;

/**
 * Created by GwonHyeok on 14. 11. 7..
 */
public class RiffleMapActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationButtonClickListener {
    private GoogleMap mMap;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_rifflemap);
        viewInit();
        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }

    private void viewInit() {
        getSupportActionBar().hide();

        ListView listView = (ListView) findViewById(R.id.campusmap_listview);
        findViewById(R.id.campusmap_hide_maplist).setOnClickListener(this);
        findViewById(R.id.campusmap_simple_list).setOnClickListener(this);
        String[] string = new String[]{"리강", "미밀스", "맥도날드", "맘스터치", "카페베네", "이디아", "하늘본닭", "스타벅스", "쌍둥이수육국밥", "라파즈", "학사주점", "신통치킨"};
        ArrayAdapter<String> mapListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, string);
        listView.setAdapter(mapListAdapter);
        listView.setOnItemClickListener(this);

        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.campusmap))
                    .getMap();

            /* Remove Zoom Button */
            mMap.getUiSettings().setZoomControlsEnabled(false);
            if (mMap != null) {
                setUpMap("geo:37.2746936,127.1313567");
            }
        }
    }

    private void setUpMap(String location) {
        /**
         * 다이얼로그에서 지도 좌표를 받아서 넘기고 그 좌표값으로 마커 설정. (String geo)
         * geo = geo:0.000000,0.000000
         */
        mMap.clear();
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker);
        String M_geo = location.split(":")[1];
        double x_geo = Double.parseDouble(M_geo.split(",")[0]); //x좌표.
        double y_geo = Double.parseDouble(M_geo.split(",")[1]); //y좌표
        mMap.addMarker(new MarkerOptions().position(new LatLng(x_geo, y_geo)).title("").icon(icon));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(x_geo, y_geo), 16.0f));
        mMap.setOnMapClickListener(this); //Map Click Listener
        mMap.setOnMyLocationButtonClickListener(this); //My Location Button Click Listener
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }
}
