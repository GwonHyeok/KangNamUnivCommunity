package com.yscn.knucommunity.Activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 7..
 */
public class RiffleMapActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationButtonClickListener {
    private GoogleMap mMap;
    private RiffleMapAdapter mapListAdapter;

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
        mapListAdapter = new RiffleMapAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<RiffleMapItem>());
        listView.setAdapter(mapListAdapter);
        listView.setOnItemClickListener(this);

        setRiffleDataInfo();
        setUpMapIfNeeded();
    }

    private void setRiffleDataInfo() {
        new AsyncTask<Void, Void, JSONObject>() {
            private ClearProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ClearProgressDialog(RiffleMapActivity.this);
                dialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().getCouncilWelfareInfo();
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                dialog.cancel();
                if (jsonObject == null) {
                    AlertToast.error(getApplicationContext(), R.string.error_to_work);
                    return;
                }
                String result = jsonObject.get("result").toString();
                if (result.equals("success")) {
                    mapListAdapter.clearItems();
                    JSONArray jsonArray = (JSONArray) jsonObject.get("data");
                    for (Object object : jsonArray) {
                        JSONObject dataObject = (JSONObject) object;
                        String shopname = dataObject.get("shopname").toString();
                        String geo = dataObject.get("geo").toString();
                        mapListAdapter.addItem(new RiffleMapItem(geo, shopname));
                    }
                    mapListAdapter.notifyDataSetChanged();
                }
            }
        }.execute();
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
        Log.d(getClass().getSimpleName(), location);
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
        TextView textView = (TextView) findViewById(R.id.campusmap_simple_text);
        findViewById(R.id.campusmap_hide_maplist).performClick();

        // 37.273798, 127.127271
        String geo = mapListAdapter.mItems.get(i).getGeo();
        String shopname = mapListAdapter.mItems.get(i).getName();
        geo = "geo:" + geo.replace(" ", "");
        setUpMap(geo);
        textView.setText(shopname);
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
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    private class RiffleMapAdapter extends ArrayAdapter<ArrayList<RiffleMapItem>> {
        int mResourceId;
        private ArrayList<RiffleMapItem> mItems;

        public RiffleMapAdapter(Context context, int resource, ArrayList<RiffleMapItem> items) {
            super(context, resource);
            this.mResourceId = resource;
            this.mItems = items;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            RiffleMapViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(this.getContext()).inflate(mResourceId, parent, false);
                viewHolder = new RiffleMapViewHolder();
                viewHolder.simpleTextview = (TextView) view.findViewById(android.R.id.text1);
                view.setTag(viewHolder);
                ApplicationUtil.getInstance().setTypeFace(view);
            } else {
                view = convertView;
                viewHolder = (RiffleMapViewHolder) view.getTag();
            }
            viewHolder.simpleTextview.setText(mItems.get(position).getName());
            return view;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        public void clearItems() {
            mItems.clear();
        }

        public void addItem(RiffleMapItem riffleMapItem) {
            mItems.add(riffleMapItem);
        }

        private class RiffleMapViewHolder {
            private TextView simpleTextview;
        }
    }

    private class RiffleMapItem {
        private String geo, name;

        public RiffleMapItem(String geo, String name) {
            this.geo = geo;
            this.name = name;
        }

        public String getGeo() {
            return geo;
        }

        public String getName() {
            return name;
        }
    }
}
