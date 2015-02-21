package com.yscn.knucommunity.Activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.ApplicationUtil;

/**
 * Created by GwonHyeok on 14. 11. 6..
 */
public class CampusMapActivity extends ActionBarActivity implements GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationButtonClickListener, View.OnClickListener, AdapterView.OnItemClickListener {
    private final String GEO_EGONG = "geo:37.2770829,127.1341592";
    private String CURRENT_GEO = GEO_EGONG;
    private final String GEO_GYEONCHEON = "geo:37.2765152,127.1339019";
    private final String GEO_HUSENG = "geo:37.2769126,127.1335131";
    private final String GEO_CHEONN = "geo:37.2757035,127.1341903";
    private final String GEO_GOYUK = "geo:37.275306,127.1332509";
    private final String GEO_SEUNGLEE = "geo:37.274445,127.1323785";
    private final String GEO_SHALROM = "geo:37.2749354,127.1300127";
    private final String GEO_MOKYANG = "geo:37.2741456,127.1319862";
    private final String GEO_WUWON = "geo:37.2757826,127.1316117";
    private final String GEO_INSA = "geo:37.2752595,127.1307101";
    private final String GEO_YESUL = "geo:37.27607,127.1309304";
    private final String GEO_UNDONG = "geo:37.2746936,127.1313567";
    private GoogleMap mMap;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_campusmap);
        getSupportActionBar().hide();

        setUpMapIfNeeded();

        ListView listView = (ListView) findViewById(R.id.campusmap_listview);
        /* Currently Click Show Animation */
        findViewById(R.id.campusmap_hide_maplist).setOnClickListener(this);
        findViewById(R.id.campusmap_simple_list).setOnClickListener(this);

        String[] string = new String[]{"이공관", "천은관", "후생관", "샬롬관", "경천관", "교욱관", "승리관", "목양관", "우원관", "인사관", "예술관", "운동장"};

        class CampusMapAdapter extends ArrayAdapter<String> {
            int mResourceId;

            public CampusMapAdapter(Context context, int resource, String[] objects) {
                super(context, resource, objects);
                this.mResourceId = resource;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;
                CampusMapViewHolder viewHolder;
                if (convertView == null) {
                    view = LayoutInflater.from(this.getContext()).inflate(mResourceId, parent, false);
                    viewHolder = new CampusMapViewHolder();
                    viewHolder.simpleTextview = (TextView) view.findViewById(android.R.id.text1);
                    view.setTag(viewHolder);
                    ApplicationUtil.getInstance().setTypeFace(view);
                } else {
                    view = convertView;
                    viewHolder = (CampusMapViewHolder) view.getTag();
                }
                viewHolder.simpleTextview.setText(getItem(position));
                return view;
            }

            class CampusMapViewHolder {
                private TextView simpleTextview;
            }
        }

        CampusMapAdapter mapListAdapter = new CampusMapAdapter(this, android.R.layout.simple_list_item_1, string);
        listView.setAdapter(mapListAdapter);
        listView.setOnItemClickListener(this);
        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.campusmap))
                    .getMap();

            /* Remove Zoom Button */
            mMap.getUiSettings().setZoomControlsEnabled(false);
            if (mMap != null) {
                setUpMap(GEO_EGONG);
            }
        }
    }

    private void setUpMap(String location) {
        /**
         * 다이얼로그에서 지도 좌표를 받아서 넘기고 그 좌표값으로 마커 설정. (String geo)
         * geo = geo:0.000000,0.000000
         */
        CURRENT_GEO = location;
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
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.campusmap_hide_maplist) {
            View panelView = findViewById(R.id.campusmap_main_list);
            panelView.startAnimation(getSlideDownAnimation(panelView, true));
        } else if (id == R.id.campusmap_simple_list) {
            View showPanelView = findViewById(R.id.campusmap_simple_list);
            showPanelView.startAnimation(getSlideDownAnimation(showPanelView, false));
        }
    }

    private Animation getSlideDownAnimation(final View view, final boolean isMainList) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slidedown);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                if (isMainList) {
                    findViewById(R.id.campusmap_simple_list).setVisibility(View.VISIBLE);
                    findViewById(R.id.campusmap_simple_list).startAnimation(getSlideUpAnimation());
                } else {
                    findViewById(R.id.campusmap_main_list).setVisibility(View.VISIBLE);
                    findViewById(R.id.campusmap_main_list).startAnimation(getSlideUpAnimation());
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return animation;
    }

    private Animation getSlideUpAnimation() {
        return AnimationUtils.loadAnimation(this, R.anim.slideup);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView textView = (TextView) findViewById(R.id.campusmap_simple_text);
        switch (i) {
            case 0:
                textView.setText("강남대학교 이공관");
                setUpMap(GEO_EGONG);
                break;
            case 1:
                textView.setText("강남대학교 천은관");
                setUpMap(GEO_CHEONN);
                break;
            case 2:
                textView.setText("강남대학교 후생관");
                setUpMap(GEO_HUSENG);
                break;
            case 3:
                textView.setText("강남대학교 샬롬관");
                setUpMap(GEO_SHALROM);
                break;
            case 4:
                textView.setText("강남대학교 경천관");
                setUpMap(GEO_GYEONCHEON);
                break;
            case 5:
                textView.setText("강남대학교 교육관");
                setUpMap(GEO_GOYUK);
                break;
            case 6:
                textView.setText("강남대학교 승리관");
                setUpMap(GEO_SEUNGLEE);
                break;
            case 7:
                textView.setText("강남대학교 목양관");
                setUpMap(GEO_MOKYANG);
                break;
            case 8:
                textView.setText("강남대학교 우원관");
                setUpMap(GEO_WUWON);
                break;
            case 9:
                textView.setText("강남대학교 인사관");
                setUpMap(GEO_INSA);
                break;
            case 10:
                textView.setText("강남대학교 예술관");
                setUpMap(GEO_YESUL);
                break;
            case 11:
                textView.setText("강남대학교 운동장");
                setUpMap(GEO_UNDONG);
                break;
        }
        findViewById(R.id.campusmap_hide_maplist).performClick();
    }

    public void startMapApp(View view) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(CURRENT_GEO));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            AlertToast.error(this, R.string.error_notexist_map_app);
        }
    }
}
