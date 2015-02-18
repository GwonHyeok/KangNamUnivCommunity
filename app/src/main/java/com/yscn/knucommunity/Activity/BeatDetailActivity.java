package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by GwonHyeok on 15. 2. 19..
 */
public class BeatDetailActivity extends ActionBarActivity {
    private int mBeatIndex;
    private String mContentId;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_beat_detail);
        setBeatIntentData();
        setBeatContentData();
    }

    private void setBeatContentData() {
        new AsyncTask<Void, Void, JSONObject>() {
            private ProgressBar mProgressbar;

            @Override
            protected void onPreExecute() {
                mProgressbar = (ProgressBar) findViewById(R.id.beat_detail_progressbar);
                mProgressbar.setVisibility(View.VISIBLE);
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                HashMap<String, String> parameter = new HashMap<>();
                parameter.put("beatid", String.valueOf(mBeatIndex + 1));
                parameter.put("contentid", mContentId);
                try {
                    return NetworkUtil.getInstance().getBeatDetail(parameter);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                mProgressbar.setVisibility(View.GONE);
                Log.d(getClass().getSimpleName(), jsonObject.toJSONString());
//                {"result":"success","data":[{"studentnumber":null,"title":"문화 누리카드 특별할인","beatid":"1","time":"2015-02-18 17:23:00","content":"이 달의 룩엔룩 최고의 좋아요를 기록한 학생은 컴미공의 14학번 꼬녁 학생이 차지했습니다. 그럼 그 학생의 아름다운 패션을 볼까요?!","id":"5"}]}
                try {
                    ((TextView) findViewById(R.id.beat_detail_title)).setText(jsonObject.get("title").toString());
                    ((TextView) findViewById(R.id.beat_detail_content)).setText(jsonObject.get("content").toString());
                } catch (Exception e) {

                }

            }
        }.execute();
    }

    private void setBeatIntentData() {
        Intent intent = getIntent();
        mBeatIndex = intent.getIntExtra("beatindex", -1);
        mContentId = intent.getStringExtra("contentid");
    }
}
